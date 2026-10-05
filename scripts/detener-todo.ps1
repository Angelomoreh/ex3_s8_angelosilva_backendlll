$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

docker compose down
if ($LASTEXITCODE -ne 0) {
    throw "No fue posible detener todos los contenedores"
}

Write-Host "Todos los servicios fueron detenidos. Los datos se conservan." -ForegroundColor Green
