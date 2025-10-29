# Quick environment check script

Write-Host "=== Environment Check ===" -ForegroundColor Cyan
Write-Host ""

# Check if java command works
Write-Host "1. Checking 'java' command..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "[OK] Java command found!" -ForegroundColor Green
    Write-Host $javaVersion
} catch {
    Write-Host "[ERROR] Java command not found" -ForegroundColor Red
    Write-Host "Java is not in your PATH for this PowerShell session" -ForegroundColor Yellow
}

Write-Host ""

# Check JAVA_HOME environment variable
Write-Host "2. Checking JAVA_HOME variable..." -ForegroundColor Yellow
if ($env:JAVA_HOME) {
    Write-Host "[OK] JAVA_HOME = $env:JAVA_HOME" -ForegroundColor Green

    # Check if the path exists
    if (Test-Path $env:JAVA_HOME) {
        Write-Host "[OK] Path exists" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Path does not exist!" -ForegroundColor Red
    }
} else {
    Write-Host "[ERROR] JAVA_HOME is not set in this session" -ForegroundColor Red
}

Write-Host ""

# Check PATH variable for Java
Write-Host "3. Checking PATH for Java..." -ForegroundColor Yellow
$paths = $env:PATH -split ';'
$javaInPath = $paths | Where-Object { $_ -like "*java*" -or $_ -like "*jdk*" -or $_ -like "*jre*" }

if ($javaInPath) {
    Write-Host "[OK] Found Java-related paths:" -ForegroundColor Green
    $javaInPath | ForEach-Object { Write-Host "  - $_" -ForegroundColor White }
} else {
    Write-Host "[ERROR] No Java paths found in PATH" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Solution ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "If you just installed Java or changed environment variables:" -ForegroundColor Yellow
Write-Host "1. Close this PowerShell window completely" -ForegroundColor White
Write-Host "2. Open a NEW PowerShell window" -ForegroundColor White
Write-Host "3. Navigate back to: cd 'd:\_My Projects\Jewel'" -ForegroundColor White
Write-Host "4. Run this script again: .\check-env.ps1" -ForegroundColor White
Write-Host ""
Write-Host "If Java still isn't found after restarting PowerShell:" -ForegroundColor Yellow
Write-Host "Run: .\fix-java-path.ps1" -ForegroundColor White
