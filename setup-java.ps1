# PowerShell script to check Java installation and provide setup instructions

Write-Host "=== Java Setup Check ===" -ForegroundColor Cyan
Write-Host ""

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString() }
    Write-Host "Java found: $javaVersion" -ForegroundColor Green

    # Check Java version
    if ($javaVersion -match "17\.|\`"17") {
        Write-Host "Java 17 detected - Perfect!" -ForegroundColor Green

        # Check JAVA_HOME
        if ($env:JAVA_HOME) {
            Write-Host "[OK] JAVA_HOME is set to: $env:JAVA_HOME" -ForegroundColor Green
        } else {
            Write-Host "[WARNING] JAVA_HOME is not set" -ForegroundColor Yellow
            Write-Host ""
            Write-Host "Would you like to set JAVA_HOME for this session? (y/n)" -ForegroundColor Yellow
            $response = Read-Host

            if ($response -eq "y") {
                # Try to find Java installation
                $javaExe = (Get-Command java).Source
                $javaHome = Split-Path (Split-Path $javaExe -Parent) -Parent

                Write-Host "Setting JAVA_HOME to: $javaHome" -ForegroundColor Green
                $env:JAVA_HOME = $javaHome

                Write-Host ""
                Write-Host "To make this permanent, add to System Environment Variables:" -ForegroundColor Cyan
                Write-Host "1. Search 'Environment Variables' in Windows" -ForegroundColor White
                Write-Host "2. Click 'Environment Variables'" -ForegroundColor White
                Write-Host "3. Under System Variables, click 'New'" -ForegroundColor White
                Write-Host "4. Variable name: JAVA_HOME" -ForegroundColor White
                Write-Host "5. Variable value: $javaHome" -ForegroundColor White
            }
        }

        Write-Host ""
        Write-Host "[READY] You're ready to build! Run: .\gradlew.bat build" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Wrong Java version detected" -ForegroundColor Red
        Write-Host "  You need Java 17 for Minecraft 1.20.1 Forge" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Please install Java 17 from:" -ForegroundColor Yellow
        Write-Host "  https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Cyan
    }
} catch {
    Write-Host "[ERROR] Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "=== Installation Instructions ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Download Java 17 (Eclipse Temurin):" -ForegroundColor Yellow
    Write-Host "   https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "2. Run the installer" -ForegroundColor Yellow
    Write-Host "   - Choose 'Add to PATH' during installation" -ForegroundColor White
    Write-Host "   - Choose 'Set JAVA_HOME' during installation" -ForegroundColor White
    Write-Host ""
    Write-Host "3. Restart PowerShell after installation" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "4. Run this script again to verify: .\setup-java.ps1" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== End of Java Setup Check ===" -ForegroundColor Cyan
