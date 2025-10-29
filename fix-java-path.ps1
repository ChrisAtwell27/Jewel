# Script to manually refresh environment variables in current PowerShell session

Write-Host "=== Refreshing Environment Variables ===" -ForegroundColor Cyan
Write-Host ""

# Function to get registry value
function Get-EnvironmentVariable {
    param([string]$Name, [string]$Scope)

    if ($Scope -eq "Machine") {
        $path = "HKLM:\SYSTEM\CurrentControlSet\Control\Session Manager\Environment"
    } else {
        $path = "HKCU:\Environment"
    }

    try {
        return (Get-ItemProperty -Path $path -Name $Name -ErrorAction Stop).$Name
    } catch {
        return $null
    }
}

Write-Host "Reading environment variables from registry..." -ForegroundColor Yellow

# Get JAVA_HOME from System (Machine) variables
$javaHomeMachine = Get-EnvironmentVariable "JAVA_HOME" "Machine"
if ($javaHomeMachine) {
    Write-Host "[Found] System JAVA_HOME: $javaHomeMachine" -ForegroundColor Green
    $env:JAVA_HOME = $javaHomeMachine
} else {
    # Check User variables
    $javaHomeUser = Get-EnvironmentVariable "JAVA_HOME" "User"
    if ($javaHomeUser) {
        Write-Host "[Found] User JAVA_HOME: $javaHomeUser" -ForegroundColor Green
        $env:JAVA_HOME = $javaHomeUser
    }
}

# Get PATH from both System and User
$pathMachine = Get-EnvironmentVariable "Path" "Machine"
$pathUser = Get-EnvironmentVariable "Path" "User"

# Combine and set
$env:Path = "$pathMachine;$pathUser"

Write-Host ""
Write-Host "Environment variables refreshed!" -ForegroundColor Green
Write-Host ""

# Test if Java works now
Write-Host "Testing Java..." -ForegroundColor Yellow
try {
    $javaTest = java -version 2>&1
    Write-Host "[SUCCESS] Java is now working!" -ForegroundColor Green
    Write-Host $javaTest
    Write-Host ""
    Write-Host "You can now run: .\gradlew.bat build" -ForegroundColor Cyan
} catch {
    Write-Host "[FAILED] Java still not found" -ForegroundColor Red
    Write-Host ""
    Write-Host "=== Manual Fix Required ===" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Please check your Java installation:" -ForegroundColor White
    Write-Host "1. Open Windows Settings" -ForegroundColor White
    Write-Host "2. Search 'Environment Variables'" -ForegroundColor White
    Write-Host "3. Check System Variables for:" -ForegroundColor White
    Write-Host "   - JAVA_HOME (should point to your JDK folder)" -ForegroundColor White
    Write-Host "   - Path (should include %JAVA_HOME%\bin)" -ForegroundColor White
    Write-Host ""
    Write-Host "Common Java locations:" -ForegroundColor Yellow
    Write-Host "  C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot" -ForegroundColor White
    Write-Host "  C:\Program Files\Java\jdk-17.x.x" -ForegroundColor White
    Write-Host "  C:\Program Files\OpenJDK\jdk-17.x.x" -ForegroundColor White
}
