$ErrorActionPreference = "Stop"

$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

[Console]::InputEncoding = $Utf8NoBom
[Console]::OutputEncoding = $Utf8NoBom
$OutputEncoding = $Utf8NoBom

$ProjectRoot = Resolve-Path (
    Join-Path $PSScriptRoot "../.."
)

$LogDirectory = Join-Path $ProjectRoot "backups/logs"
$LogFile = Join-Path $LogDirectory "database-backup.log"

New-Item `
    -ItemType Directory `
    -Force `
    -Path $LogDirectory | Out-Null

function Write-Log {
    param (
        [Parameter(Mandatory)]
        [string]$Message
    )

    $Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $LogLine = "[$Timestamp] $Message"

    [System.IO.File]::AppendAllText(
        $LogFile,
        $LogLine + [Environment]::NewLine,
        $Utf8NoBom
    )
}

try {
Write-Log "Debut de la sauvegarde"

    Set-Location $ProjectRoot

    $env:DB_PASSWORD = "cesizen_local_password"
    $env:BACKUP_RETENTION_DAYS = "14"

    $BashOutput = & "C:\Program Files\Git\bin\bash.exe" `
        "./scripts/database/backup.sh" 2>&1

    $ExitCode = $LASTEXITCODE

    foreach ($Line in $BashOutput) {
        [System.IO.File]::AppendAllText(
            $LogFile,
            [string]$Line + [Environment]::NewLine,
            $Utf8NoBom
        )
    }

    if ($ExitCode -ne 0) {
        throw "Le script de sauvegarde a retourné le code $ExitCode."
    }

Write-Log "Sauvegarde terminee avec succes"
}
catch {
Write-Log "ECHEC : $($_.Exception.Message)"
    exit 1
}
finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:BACKUP_RETENTION_DAYS -ErrorAction SilentlyContinue
}
