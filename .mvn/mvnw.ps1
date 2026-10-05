$ErrorActionPreference = "Stop"

$MavenVersion = "3.9.16"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$MavenCommand = $null

$InstalledMaven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($InstalledMaven) {
    $MavenCommand = $InstalledMaven.Source
}

if (-not $MavenCommand) {
    $CachedMaven = Get-ChildItem -Path "$env:USERPROFILE\.m2\wrapper\dists" -Filter "mvn.cmd" -Recurse -ErrorAction SilentlyContinue |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1
    if ($CachedMaven) {
        $MavenCommand = $CachedMaven.FullName
    }
}

if (-not $MavenCommand) {
    $LocalMaven = Join-Path $ProjectRoot ".mvn\apache-maven-$MavenVersion\bin\mvn.cmd"
    if (-not (Test-Path $LocalMaven)) {
        $Archive = Join-Path $ProjectRoot ".mvn\apache-maven-$MavenVersion-bin.zip"
        $Url = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MavenVersion/apache-maven-$MavenVersion-bin.zip"
        Write-Host "Maven no está instalado. Descargando Maven $MavenVersion..." -ForegroundColor Yellow
        Invoke-WebRequest -Uri $Url -OutFile $Archive -UseBasicParsing
        Expand-Archive -Path $Archive -DestinationPath (Join-Path $ProjectRoot ".mvn") -Force
    }
    $MavenCommand = $LocalMaven
}

Push-Location $ProjectRoot
try {
    & $MavenCommand @args
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
