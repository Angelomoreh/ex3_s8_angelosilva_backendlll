$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot

function Wait-Url {
    param(
        [string]$Url,
        [string]$Nombre,
        [int]$Intentos = 120
    )

    for ($i = 1; $i -le $Intentos; $i++) {
        try {
            $Response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 3
            if ($Response.StatusCode -eq 200) {
                Write-Host "$Nombre disponible" -ForegroundColor Green
                return
            }
        }
        catch {
            Start-Sleep -Seconds 2
        }
    }
    throw "$Nombre no respondio en $Url"
}

function Wait-EurekaApplications {
    param([int]$CantidadEsperada = 6, [int]$Intentos = 120)

    for ($i = 1; $i -le $Intentos; $i++) {
        try {
            $Registry = Invoke-RestMethod `
                -Uri "http://localhost:8761/eureka/apps" `
                -Headers @{ Accept = "application/json" } `
                -TimeoutSec 3
            $Cantidad = @($Registry.applications.application).Count
            if ($Cantidad -ge $CantidadEsperada) {
                Write-Host "$Cantidad servicios registrados en Eureka" -ForegroundColor Green
                return
            }
        }
        catch {
        }
        Start-Sleep -Seconds 2
    }
    throw "Los microservicios no alcanzaron a registrarse en Eureka"
}

Set-Location $Root
Write-Host "Construyendo imagenes y levantando la arquitectura..." -ForegroundColor Cyan
docker compose up --build -d
if ($LASTEXITCODE -ne 0) {
    throw "Docker Compose no pudo iniciar la solucion"
}

Write-Host "Esperando los componentes principales..." -ForegroundColor Cyan
Wait-Url "http://localhost:8888/actuator/health" "Config Server"
Wait-Url "http://localhost:8761/actuator/health" "Eureka Server"
Wait-Url "http://localhost:8180/realms/banco-xyz/.well-known/openid-configuration" "OAuth2 Keycloak"
Wait-Url "http://localhost:8080/actuator/health" "API Gateway"
Wait-EurekaApplications

Write-Host ""
docker compose ps
Write-Host ""
Write-Host "ARQUITECTURA BANCO XYZ DISPONIBLE" -ForegroundColor Green
Write-Host "Gateway seguro: http://localhost:8080"
Write-Host "Eureka:         http://localhost:8761"
Write-Host "Config Server:  http://localhost:8888"
Write-Host "Keycloak:       http://localhost:8180"
Write-Host ""
Write-Host "Para ejecutar todas las pruebas usa: .\probar-apis.cmd"
