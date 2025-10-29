# PowerShell script to download Gradle wrapper JAR

Write-Host "Setting up Gradle wrapper..." -ForegroundColor Green

# Create gradle/wrapper directory if it doesn't exist
$wrapperDir = "gradle/wrapper"
if (!(Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
}

# Download gradle-wrapper.jar
$wrapperJarUrl = "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"
$wrapperJarPath = "$wrapperDir/gradle-wrapper.jar"

Write-Host "Downloading gradle-wrapper.jar..." -ForegroundColor Yellow
try {
    Invoke-WebRequest -Uri $wrapperJarUrl -OutFile $wrapperJarPath
    Write-Host "Successfully downloaded gradle-wrapper.jar" -ForegroundColor Green
} catch {
    Write-Host "Failed to download gradle-wrapper.jar: $_" -ForegroundColor Red
    Write-Host "Please download manually from: $wrapperJarUrl" -ForegroundColor Yellow
    exit 1
}

Write-Host "`nGradle wrapper setup complete!" -ForegroundColor Green
Write-Host "You can now run: .\gradlew.bat build" -ForegroundColor Cyan
