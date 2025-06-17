param(
    [string]$PhpIniPath = ""
)

# Default XAMPP php.ini paths
$defaultPaths = @(
    "C:\xampp\php\php.ini",
    "C:\XAMPP\php\php.ini",
    "$env:ProgramFiles\xampp\php\php.ini",
    "${env:ProgramFiles(x86)}\xampp\php\php.ini"
)

function Find-PhpIni {
    if ($PhpIniPath -and (Test-Path $PhpIniPath)) {
        return $PhpIniPath
    }
    
    foreach ($path in $defaultPaths) {
        if (Test-Path $path) {
            Write-Host "Found php.ini at: $path" -ForegroundColor Green
            return $path
        }
    }
    
    Write-Error "php.ini not found in default XAMPP locations. Please specify the path."
    return $null
}

function Remove-ExtensionSemicolons {
    param([string]$FilePath)
    
    if (-not (Test-Path $FilePath)) {
        Write-Error "File not found: $FilePath"
        return $false
    }
    
    # Create backup
    $backupPath = "$FilePath.backup"
    Copy-Item $FilePath $backupPath
    Write-Host "Backup created: $backupPath" -ForegroundColor Blue
    
    # Read file content
    $content = Get-Content $FilePath
    $modified = $false
    $extensions = @('curl', 'gd', 'imagick', 'redis', 'intl', 'mysqli', 'pdo_mysql')

    for ($i = 0; $i -lt $content.Length; $i++) {
        $line = $content[$i]
        
        # Check if line matches ;extension=<target_extension>
        foreach ($ext in $extensions) {
            if ($line -match "^;\s*extension\s*=\s*$ext\s*$") {
                $content[$i] = $line -replace '^;\s*', ''
                Write-Host "Uncommented: $line" -ForegroundColor Yellow
                $modified = $true
                break
            }
        }
    }
    
    if ($modified) {
        $content | Set-Content $FilePath
        Write-Host "Successfully updated $FilePath" -ForegroundColor Green
        Write-Host "Please restart Apache/PHP service for changes to take effect." -ForegroundColor Cyan
    } else {
        Write-Host "No matching extension lines found to uncomment." -ForegroundColor Yellow
        Remove-Item $backupPath
    }
    
    return $modified
}

# Main execution
$phpIniFile = Find-PhpIni
if ($phpIniFile) {
    Write-Host "Processing: $phpIniFile" -ForegroundColor Cyan
    Remove-ExtensionSemicolons -FilePath $phpIniFile
    
    # Show PHP configuration info
    Write-Host "`nPHP Configuration Information:" -ForegroundColor Magenta
    try {
        php --ini
    } catch {
        Write-Warning "Could not execute 'php --ini'. Make sure PHP is in your PATH."
    }
} else {
    Write-Host "Usage: .\remove_semicolons.ps1 [path_to_php.ini]" -ForegroundColor Red
}
