$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Output = Join-Path $Root "evidencias\salidas"
New-Item -ItemType Directory -Path $Output -Force | Out-Null

function Save-Json {
    param([string]$Nombre, [object]$Contenido)
    $Json = $Contenido | ConvertTo-Json -Depth 15
    Set-Content -Path (Join-Path $Output $Nombre) -Value $Json -Encoding UTF8
    Write-Host "OK $Nombre" -ForegroundColor Green
}

function Save-Text {
    param([string]$Nombre, [string]$Contenido)
    Set-Content -Path (Join-Path $Output $Nombre) -Value $Contenido -Encoding UTF8
    Write-Host "OK $Nombre" -ForegroundColor Green
}

Write-Host "1. Comprobando OAuth2 y seguridad del Gateway..." -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/web/cuentas/101" | Out-Null
    throw "El Gateway permitio una solicitud sin token"
}
catch {
    $StatusCode = $_.Exception.Response.StatusCode.value__
    if ($StatusCode -ne 401) {
        throw
    }
    Save-Text "01_oauth_sin_token_401.txt" "Solicitud sin token OAuth2 rechazada correctamente con HTTP 401 Unauthorized."
}

$TokenResponse = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8180/realms/banco-xyz/protocol/openid-connect/token" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body @{
        grant_type = "password"
        client_id  = "banco-api"
        username   = "angelo"
        password   = "angelo123"
    }

if (-not $TokenResponse.access_token) {
    throw "Keycloak no entrego un access_token"
}

$AuthHeaders = @{ Authorization = "Bearer $($TokenResponse.access_token)" }
$AtmHeaders = @{
    Authorization = "Bearer $($TokenResponse.access_token)"
    "X-ATM-KEY"  = "BANCO-XYZ-ATM-2026"
}
Save-Text "02_oauth_token_obtenido.txt" "OAuth2 entrego un access_token JWT funcional para el usuario angelo y el cliente banco-api."

Write-Host "2. Comprobando Config Server y Eureka..." -ForegroundColor Cyan
$Config = Invoke-RestMethod -Uri "http://localhost:8888/bff-web/default"
Save-Json "03_config_server.json" $Config
$Eureka = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Headers @{ Accept = "application/json" }
Save-Json "04_eureka_servicios.json" $Eureka

Write-Host "3. Probando los BFF mediante el Gateway seguro..." -ForegroundColor Cyan
$Web = Invoke-RestMethod -Uri "http://localhost:8080/api/web/cuentas/101" -Headers $AuthHeaders
Save-Json "05_bff_web_seguro.json" $Web
$Mobile = Invoke-RestMethod -Uri "http://localhost:8080/api/mobile/cuentas/101" -Headers $AuthHeaders
Save-Json "06_bff_mobile_seguro.json" $Mobile
$AtmSaldo = Invoke-RestMethod -Uri "http://localhost:8080/api/atm/cuentas/101/saldo" -Headers $AtmHeaders
Save-Json "07_bff_atm_seguro.json" $AtmSaldo

try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/atm/cuentas/101/saldo" -Headers $AuthHeaders | Out-Null
    throw "El BFF ATM permitio una solicitud sin X-ATM-KEY"
}
catch {
    $StatusCode = $_.Exception.Response.StatusCode.value__
    if ($StatusCode -ne 401) {
        throw
    }
    Save-Text "08_atm_sin_api_key_401.txt" "Solicitud ATM con JWT pero sin X-ATM-KEY rechazada correctamente con HTTP 401."
}

Write-Host "4. Probando retiro y evento asincrono Kafka..." -ForegroundColor Cyan
$Body = @{ monto = 500 } | ConvertTo-Json
$Retiro = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/api/atm/cuentas/101/retiros" `
    -Headers $AtmHeaders `
    -ContentType "application/json" `
    -Body $Body
Save-Json "09_retiro_publicado_kafka.json" $Retiro
Start-Sleep -Seconds 4

$WebDespues = Invoke-RestMethod -Uri "http://localhost:8080/api/web/cuentas/101" -Headers $AuthHeaders
Save-Json "10_transaccion_consumida_kafka.json" $WebDespues
$KafkaLogs = docker logs banco-cloud-transacciones --tail 80 2>&1 | Out-String
Save-Text "11_logs_consumidor_kafka.txt" $KafkaLogs
$DatosSql = docker exec banco-cloud-postgres psql -U banco_user -d banco_cloud -c "SELECT evento_id, tipo_evento, procesado_en FROM eventos_procesados ORDER BY procesado_en DESC LIMIT 3; SELECT id, cuenta_id, tipo, monto, canal FROM transacciones WHERE cuenta_id = 101 ORDER BY id DESC LIMIT 3;" | Out-String
Save-Text "12_datos_eventos_postgresql.txt" $DatosSql

Write-Host "5. Demostrando Circuit Breaker y fallback..." -ForegroundColor Cyan
Set-Location $Root
docker compose stop transacciones-service | Out-Null
try {
    Start-Sleep -Seconds 3
    $RespuestaResiliente = Invoke-RestMethod -Uri "http://localhost:8080/api/web/cuentas/101" -Headers $AuthHeaders
    Save-Json "13_resilience4j_fallback.json" $RespuestaResiliente
}
finally {
    docker compose start transacciones-service | Out-Null
}

Write-Host ""
Write-Host "PRUEBAS FINALIZADAS CORRECTAMENTE" -ForegroundColor Green
Write-Host "Se comprobo OAuth2, Config Server, Eureka, BFF, Kafka, PostgreSQL y Resilience4j."
Write-Host "Las salidas quedaron guardadas en evidencias\salidas"
