Add-Type -AssemblyName System.Drawing

# Create 16x16 bitmap
$bmp = New-Object System.Drawing.Bitmap(16,16)
$g = [System.Drawing.Graphics]::FromImage($bmp)

# Define colors
$stone = [System.Drawing.Color]::FromArgb(120,120,120)
$light = [System.Drawing.Color]::FromArgb(200,200,200)
$quartz = [System.Drawing.Color]::FromArgb(235,235,235)
$white = [System.Drawing.Color]::FromArgb(255,255,255)
$brightWhite = [System.Drawing.Color]::FromArgb(255,255,255)
$dark = [System.Drawing.Color]::FromArgb(80,80,80)
$purple = [System.Drawing.Color]::FromArgb(220,220,240)

# Fill base with stone color
$g.Clear($stone)

# Create quartz outer layer
$quartzBrush = New-Object System.Drawing.SolidBrush($quartz)
$g.FillRectangle($quartzBrush, 2, 2, 12, 12)

# Create white center area
$whiteBrush = New-Object System.Drawing.SolidBrush($white)
$g.FillRectangle($whiteBrush, 4, 4, 8, 8)

# Create bright white center
$brightBrush = New-Object System.Drawing.SolidBrush($brightWhite)
$g.FillRectangle($brightBrush, 5, 5, 6, 6)

# Add purple polish indicator in center
$purpleBrush = New-Object System.Drawing.SolidBrush($purple)
$g.FillRectangle($purpleBrush, 6, 6, 4, 4)

# Add light highlights
$lightBrush = New-Object System.Drawing.SolidBrush($light)
$g.FillRectangle($lightBrush, 1, 1, 14, 1)
$g.FillRectangle($lightBrush, 1, 1, 1, 14)

# Add dark border
$darkBrush = New-Object System.Drawing.SolidBrush($dark)
$g.FillRectangle($darkBrush, 0, 0, 16, 1)
$g.FillRectangle($darkBrush, 0, 15, 16, 1)
$g.FillRectangle($darkBrush, 0, 0, 1, 16)
$g.FillRectangle($darkBrush, 15, 0, 1, 16)

# Save
$outputPath = "d:\_My Projects\Jewel\src\main\resources\assets\jewelcharms\textures\block\polish_station.png"
$bmp.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)

# Cleanup
$g.Dispose()
$bmp.Dispose()
$quartzBrush.Dispose()
$whiteBrush.Dispose()
$brightBrush.Dispose()
$purpleBrush.Dispose()
$lightBrush.Dispose()
$darkBrush.Dispose()

Write-Host "Polish station texture created at: $outputPath"