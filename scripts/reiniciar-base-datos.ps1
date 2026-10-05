$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

Write-Host "Esta accion elimina el volumen local y vuelve a cargar los datos legacy." -ForegroundColor Yellow
$Confirmation = Read-Host "Escribe REINICIAR para continuar"
if ($Confirmation -ne "REINICIAR") {
    Write-Host "Operacion cancelada"
    exit 0
}

docker compose down -v
if ($LASTEXITCODE -ne 0) {
    throw "No fue posible eliminar los contenedores y volumenes"
}

& "$PSScriptRoot\iniciar-todo.ps1"
