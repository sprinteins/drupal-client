New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\bin" | Out-Null
Invoke-WebRequest -Uri 'https://github.com/sprinteins/drupal-client/releases/latest/download/drupal-client-windows.exe' -OutFile $env:USERPROFILE\bin\drupal-client.exe

$Path = "$env:USERPROFILE\bin"
$containerType = [System.EnvironmentVariableTarget]::User
$persistedPaths = [Environment]::GetEnvironmentVariable('Path', $containerType) -split ';'
if ($persistedPaths -notcontains $Path) {
    $persistedPaths = $persistedPaths + $Path | where { $_ }
    [Environment]::SetEnvironmentVariable('Path', $persistedPaths -join ';', $containerType)
}
