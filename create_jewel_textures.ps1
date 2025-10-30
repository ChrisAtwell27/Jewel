Add-Type -AssemblyName System.Drawing

# Function to create a jewel texture
function Create-JewelTexture {
    param(
        [string]$outputPath,
        [bool]$isPolished
    )

    # Create 16x16 bitmap
    $bmp = New-Object System.Drawing.Bitmap(16,16)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None

    # White color for tinting (will be tinted by the color system)
    $white = [System.Drawing.Color]::FromArgb(255, 255, 255)
    $lightGray = [System.Drawing.Color]::FromArgb(220, 220, 220)
    $gray = [System.Drawing.Color]::FromArgb(180, 180, 180)
    $darkGray = [System.Drawing.Color]::FromArgb(120, 120, 120)
    $veryDark = [System.Drawing.Color]::FromArgb(60, 60, 60)
    $transparent = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)

    # Clear to transparent
    $g.Clear($transparent)

    if ($isPolished) {
        # Polished jewel - smooth, shiny appearance

        # Main body - diamond shape
        $whiteBrush = New-Object System.Drawing.SolidBrush($white)
        $lightBrush = New-Object System.Drawing.SolidBrush($lightGray)
        $grayBrush = New-Object System.Drawing.SolidBrush($gray)
        $darkBrush = New-Object System.Drawing.SolidBrush($darkGray)

        # Center bright core
        $g.FillRectangle($whiteBrush, 6, 6, 4, 4)
        $g.FillRectangle($whiteBrush, 7, 5, 2, 6)
        $g.FillRectangle($whiteBrush, 5, 7, 6, 2)

        # Light areas (top-left highlight)
        $g.FillRectangle($lightBrush, 4, 4, 8, 8)
        $g.FillRectangle($lightBrush, 5, 3, 6, 10)
        $g.FillRectangle($lightBrush, 3, 5, 10, 6)

        # Mid-tones
        $g.FillRectangle($grayBrush, 3, 3, 10, 1)
        $g.FillRectangle($grayBrush, 3, 12, 10, 1)
        $g.FillRectangle($grayBrush, 3, 3, 1, 10)
        $g.FillRectangle($grayBrush, 12, 3, 1, 10)

        # Dark edges (bottom-right shadow)
        $g.FillRectangle($darkBrush, 2, 2, 1, 12)
        $g.FillRectangle($darkBrush, 13, 2, 1, 12)
        $g.FillRectangle($darkBrush, 2, 2, 12, 1)
        $g.FillRectangle($darkBrush, 2, 13, 12, 1)

        # Corner pixels
        $g.FillRectangle($darkBrush, 2, 2, 1, 1)
        $g.FillRectangle($darkBrush, 13, 2, 1, 1)
        $g.FillRectangle($darkBrush, 2, 13, 1, 1)
        $g.FillRectangle($darkBrush, 13, 13, 1, 1)

        # Facet lines for crystal effect
        $veryDarkBrush = New-Object System.Drawing.SolidBrush($veryDark)
        # Vertical
        for ($y = 4; $y -lt 12; $y++) {
            $bmp.SetPixel(6, $y, $veryDark)
            $bmp.SetPixel(9, $y, $veryDark)
        }
        # Horizontal
        for ($x = 4; $x -lt 12; $x++) {
            $bmp.SetPixel($x, 6, $veryDark)
            $bmp.SetPixel($x, 9, $veryDark)
        }

        $whiteBrush.Dispose()
        $lightBrush.Dispose()
        $grayBrush.Dispose()
        $darkBrush.Dispose()
        $veryDarkBrush.Dispose()

    } else {
        # Rough jewel - grainy, unpolished appearance

        $grayBrush = New-Object System.Drawing.SolidBrush($gray)
        $lightBrush = New-Object System.Drawing.SolidBrush($lightGray)
        $darkBrush = New-Object System.Drawing.SolidBrush($darkGray)
        $veryDarkBrush = New-Object System.Drawing.SolidBrush($veryDark)

        # Base shape - more irregular
        $g.FillRectangle($grayBrush, 4, 4, 8, 8)
        $g.FillRectangle($grayBrush, 5, 3, 6, 10)
        $g.FillRectangle($grayBrush, 3, 5, 10, 6)

        # Add grainy texture with random-looking pixels
        $random = New-Object Random(42) # Fixed seed for consistency
        for ($y = 3; $y -lt 13; $y++) {
            for ($x = 3; $x -lt 13; $x++) {
                $dist = [Math]::Sqrt(($x - 8) * ($x - 8) + ($y - 8) * ($y - 8))
                if ($dist -lt 5) {
                    $noise = $random.Next(-30, 30)
                    $value = [Math]::Max(0, [Math]::Min(255, 150 + $noise))
                    $color = [System.Drawing.Color]::FromArgb(255, $value, $value, $value)
                    $bmp.SetPixel($x, $y, $color)
                }
            }
        }

        # Dark edges
        $g.FillRectangle($darkBrush, 2, 2, 1, 12)
        $g.FillRectangle($darkBrush, 13, 2, 1, 12)
        $g.FillRectangle($darkBrush, 2, 2, 12, 1)
        $g.FillRectangle($darkBrush, 2, 13, 12, 1)

        # Very dark corners
        $g.FillRectangle($veryDarkBrush, 2, 2, 1, 1)
        $g.FillRectangle($veryDarkBrush, 13, 2, 1, 1)
        $g.FillRectangle($veryDarkBrush, 2, 13, 1, 1)
        $g.FillRectangle($veryDarkBrush, 13, 13, 1, 1)

        # Rough facet lines (less prominent)
        for ($y = 5; $y -lt 11; $y++) {
            if ($y % 2 -eq 0) { $bmp.SetPixel(7, $y, $darkGray) }
        }
        for ($x = 5; $x -lt 11; $x++) {
            if ($x % 2 -eq 0) { $bmp.SetPixel($x, 7, $darkGray) }
        }

        $grayBrush.Dispose()
        $lightBrush.Dispose()
        $darkBrush.Dispose()
        $veryDarkBrush.Dispose()
    }

    # Save
    $bmp.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
}

# Create both textures
$basePath = "d:\_My Projects\Jewel\src\main\resources\assets\jewelcharms\textures\item"

Write-Host "Creating polished jewel texture..."
Create-JewelTexture -outputPath "$basePath\jewel.png" -isPolished $true

Write-Host "Creating rough jewel texture..."
Create-JewelTexture -outputPath "$basePath\rough_jewel.png" -isPolished $false

Write-Host "Jewel textures created successfully!"
Write-Host "Polished: $basePath\jewel.png"
Write-Host "Rough: $basePath\rough_jewel.png"
