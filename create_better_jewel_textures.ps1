Add-Type -AssemblyName System.Drawing

function Create-PolishedJewel {
    param([string]$outputPath)

    $bmp = New-Object System.Drawing.Bitmap(16, 16)

    # Colors for a polished gem - pure white/grayscale for tinting
    $white = [System.Drawing.Color]::FromArgb(255, 255, 255, 255)
    $veryLight = [System.Drawing.Color]::FromArgb(255, 240, 240, 240)
    $light = [System.Drawing.Color]::FromArgb(255, 200, 200, 200)
    $medium = [System.Drawing.Color]::FromArgb(255, 160, 160, 160)
    $dark = [System.Drawing.Color]::FromArgb(255, 100, 100, 100)
    $veryDark = [System.Drawing.Color]::FromArgb(255, 60, 60, 60)
    $black = [System.Drawing.Color]::FromArgb(255, 0, 0, 0)
    $transparent = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)

    # Clear to transparent
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $bmp.SetPixel($x, $y, $transparent)
        }
    }

    # Create a faceted gem shape - octagonal with facets
    # This creates a gem that looks polished and will tint beautifully

    # Outer dark outline for definition
    $bmp.SetPixel(7, 1, $black)
    $bmp.SetPixel(8, 1, $black)
    $bmp.SetPixel(5, 2, $black)
    $bmp.SetPixel(10, 2, $black)
    $bmp.SetPixel(3, 3, $black)
    $bmp.SetPixel(12, 3, $black)
    $bmp.SetPixel(2, 5, $black)
    $bmp.SetPixel(13, 5, $black)
    $bmp.SetPixel(1, 7, $black)
    $bmp.SetPixel(14, 7, $black)
    $bmp.SetPixel(1, 8, $black)
    $bmp.SetPixel(14, 8, $black)
    $bmp.SetPixel(2, 10, $black)
    $bmp.SetPixel(13, 10, $black)
    $bmp.SetPixel(3, 12, $black)
    $bmp.SetPixel(12, 12, $black)
    $bmp.SetPixel(5, 13, $black)
    $bmp.SetPixel(10, 13, $black)
    $bmp.SetPixel(7, 14, $black)
    $bmp.SetPixel(8, 14, $black)

    # Top facet - very bright (shine/highlight)
    $bmp.SetPixel(7, 2, $white)
    $bmp.SetPixel(8, 2, $white)
    $bmp.SetPixel(6, 3, $white)
    $bmp.SetPixel(7, 3, $white)
    $bmp.SetPixel(8, 3, $white)
    $bmp.SetPixel(9, 3, $white)
    $bmp.SetPixel(6, 4, $veryLight)
    $bmp.SetPixel(7, 4, $white)
    $bmp.SetPixel(8, 4, $white)
    $bmp.SetPixel(9, 4, $veryLight)

    # Upper-left facet
    $bmp.SetPixel(4, 3, $light)
    $bmp.SetPixel(5, 3, $veryLight)
    $bmp.SetPixel(3, 4, $light)
    $bmp.SetPixel(4, 4, $light)
    $bmp.SetPixel(5, 4, $veryLight)
    $bmp.SetPixel(3, 5, $medium)
    $bmp.SetPixel(4, 5, $light)
    $bmp.SetPixel(5, 5, $light)
    $bmp.SetPixel(2, 6, $medium)
    $bmp.SetPixel(3, 6, $medium)
    $bmp.SetPixel(4, 6, $light)
    $bmp.SetPixel(5, 6, $light)

    # Upper-right facet
    $bmp.SetPixel(10, 3, $veryLight)
    $bmp.SetPixel(11, 3, $light)
    $bmp.SetPixel(10, 4, $veryLight)
    $bmp.SetPixel(11, 4, $light)
    $bmp.SetPixel(12, 4, $light)
    $bmp.SetPixel(10, 5, $light)
    $bmp.SetPixel(11, 5, $light)
    $bmp.SetPixel(12, 5, $medium)
    $bmp.SetPixel(10, 6, $light)
    $bmp.SetPixel(11, 6, $light)
    $bmp.SetPixel(12, 6, $medium)
    $bmp.SetPixel(13, 6, $medium)

    # Center facets
    $bmp.SetPixel(6, 5, $light)
    $bmp.SetPixel(7, 5, $veryLight)
    $bmp.SetPixel(8, 5, $veryLight)
    $bmp.SetPixel(9, 5, $light)
    $bmp.SetPixel(6, 6, $light)
    $bmp.SetPixel(7, 6, $light)
    $bmp.SetPixel(8, 6, $light)
    $bmp.SetPixel(9, 6, $light)

    # Middle-left facet
    $bmp.SetPixel(2, 7, $dark)
    $bmp.SetPixel(3, 7, $medium)
    $bmp.SetPixel(4, 7, $medium)
    $bmp.SetPixel(5, 7, $light)
    $bmp.SetPixel(2, 8, $dark)
    $bmp.SetPixel(3, 8, $medium)
    $bmp.SetPixel(4, 8, $medium)
    $bmp.SetPixel(5, 8, $light)

    # Middle-right facet
    $bmp.SetPixel(10, 7, $light)
    $bmp.SetPixel(11, 7, $medium)
    $bmp.SetPixel(12, 7, $medium)
    $bmp.SetPixel(13, 7, $dark)
    $bmp.SetPixel(10, 8, $light)
    $bmp.SetPixel(11, 8, $medium)
    $bmp.SetPixel(12, 8, $medium)
    $bmp.SetPixel(13, 8, $dark)

    # Center middle
    $bmp.SetPixel(6, 7, $light)
    $bmp.SetPixel(7, 7, $light)
    $bmp.SetPixel(8, 7, $light)
    $bmp.SetPixel(9, 7, $light)
    $bmp.SetPixel(6, 8, $light)
    $bmp.SetPixel(7, 8, $medium)
    $bmp.SetPixel(8, 8, $medium)
    $bmp.SetPixel(9, 8, $light)

    # Lower-left facet
    $bmp.SetPixel(2, 9, $dark)
    $bmp.SetPixel(3, 9, $dark)
    $bmp.SetPixel(4, 9, $medium)
    $bmp.SetPixel(5, 9, $medium)
    $bmp.SetPixel(3, 10, $dark)
    $bmp.SetPixel(4, 10, $dark)
    $bmp.SetPixel(5, 10, $medium)
    $bmp.SetPixel(3, 11, $veryDark)
    $bmp.SetPixel(4, 11, $dark)
    $bmp.SetPixel(5, 11, $medium)
    $bmp.SetPixel(4, 12, $veryDark)
    $bmp.SetPixel(5, 12, $dark)

    # Lower-right facet
    $bmp.SetPixel(10, 9, $medium)
    $bmp.SetPixel(11, 9, $medium)
    $bmp.SetPixel(12, 9, $dark)
    $bmp.SetPixel(13, 9, $dark)
    $bmp.SetPixel(10, 10, $medium)
    $bmp.SetPixel(11, 10, $dark)
    $bmp.SetPixel(12, 10, $dark)
    $bmp.SetPixel(10, 11, $medium)
    $bmp.SetPixel(11, 11, $dark)
    $bmp.SetPixel(12, 11, $veryDark)
    $bmp.SetPixel(10, 12, $dark)
    $bmp.SetPixel(11, 12, $veryDark)

    # Bottom center facet
    $bmp.SetPixel(6, 9, $medium)
    $bmp.SetPixel(7, 9, $medium)
    $bmp.SetPixel(8, 9, $medium)
    $bmp.SetPixel(9, 9, $medium)
    $bmp.SetPixel(6, 10, $medium)
    $bmp.SetPixel(7, 10, $dark)
    $bmp.SetPixel(8, 10, $dark)
    $bmp.SetPixel(9, 10, $medium)
    $bmp.SetPixel(6, 11, $dark)
    $bmp.SetPixel(7, 11, $dark)
    $bmp.SetPixel(8, 11, $dark)
    $bmp.SetPixel(9, 11, $dark)
    $bmp.SetPixel(6, 12, $dark)
    $bmp.SetPixel(7, 12, $veryDark)
    $bmp.SetPixel(8, 12, $veryDark)
    $bmp.SetPixel(9, 12, $dark)

    # Bottom point
    $bmp.SetPixel(6, 13, $veryDark)
    $bmp.SetPixel(7, 13, $veryDark)
    $bmp.SetPixel(8, 13, $veryDark)
    $bmp.SetPixel(9, 13, $veryDark)

    $bmp.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

function Create-RoughJewel {
    param([string]$outputPath)

    $bmp = New-Object System.Drawing.Bitmap(16, 16)

    # Colors for rough gem - darker and less saturated
    $white = [System.Drawing.Color]::FromArgb(255, 180, 180, 180)
    $veryLight = [System.Drawing.Color]::FromArgb(255, 150, 150, 150)
    $light = [System.Drawing.Color]::FromArgb(255, 120, 120, 120)
    $medium = [System.Drawing.Color]::FromArgb(255, 90, 90, 90)
    $dark = [System.Drawing.Color]::FromArgb(255, 60, 60, 60)
    $veryDark = [System.Drawing.Color]::FromArgb(255, 40, 40, 40)
    $black = [System.Drawing.Color]::FromArgb(255, 0, 0, 0)
    $transparent = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)

    # Clear to transparent
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $bmp.SetPixel($x, $y, $transparent)
        }
    }

    # Create a rougher, chunkier gem shape
    # Similar outline but with rougher internal structure

    # Outline
    $bmp.SetPixel(7, 2, $black)
    $bmp.SetPixel(8, 2, $black)
    $bmp.SetPixel(5, 3, $black)
    $bmp.SetPixel(10, 3, $black)
    $bmp.SetPixel(3, 4, $black)
    $bmp.SetPixel(12, 4, $black)
    $bmp.SetPixel(2, 6, $black)
    $bmp.SetPixel(13, 6, $black)
    $bmp.SetPixel(2, 7, $black)
    $bmp.SetPixel(13, 7, $black)
    $bmp.SetPixel(2, 9, $black)
    $bmp.SetPixel(13, 9, $black)
    $bmp.SetPixel(3, 11, $black)
    $bmp.SetPixel(12, 11, $black)
    $bmp.SetPixel(5, 12, $black)
    $bmp.SetPixel(10, 12, $black)
    $bmp.SetPixel(7, 13, $black)
    $bmp.SetPixel(8, 13, $black)

    # Rough, unpolished interior with visible grain
    # Top section - less bright than polished
    $bmp.SetPixel(6, 3, $veryLight)
    $bmp.SetPixel(7, 3, $light)
    $bmp.SetPixel(8, 3, $light)
    $bmp.SetPixel(9, 3, $veryLight)

    # Add grainy texture throughout
    $random = New-Object Random(42)
    for ($y = 4; $y -le 11; $y++) {
        for ($x = 3; $x -le 12; $x++) {
            $dx = $x - 7.5
            $dy = $y - 7.5
            $dist = [Math]::Sqrt($dx * $dx + $dy * $dy)

            if ($dist -lt 5) {
                # Base value depends on position
                $baseValue = 120 - [int]($dy * 15)
                # Add noise
                $noise = $random.Next(-25, 15)
                $value = [Math]::Max(40, [Math]::Min(180, $baseValue + $noise))
                $color = [System.Drawing.Color]::FromArgb(255, $value, $value, $value)
                $bmp.SetPixel($x, $y, $color)
            }
        }
    }

    # Add some darker cracks/fissures for rough appearance
    $bmp.SetPixel(6, 5, $dark)
    $bmp.SetPixel(9, 5, $dark)
    $bmp.SetPixel(5, 7, $veryDark)
    $bmp.SetPixel(10, 7, $veryDark)
    $bmp.SetPixel(7, 8, $dark)
    $bmp.SetPixel(6, 9, $veryDark)
    $bmp.SetPixel(9, 9, $veryDark)
    $bmp.SetPixel(7, 10, $dark)
    $bmp.SetPixel(8, 10, $dark)

    $bmp.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

$basePath = "d:\_My Projects\Jewel\src\main\resources\assets\jewelcharms\textures\item"

Write-Host "Creating polished jewel texture (grayscale for tinting)..."
Create-PolishedJewel -outputPath "$basePath\jewel.png"

Write-Host "Creating rough jewel texture (grayscale for tinting)..."
Create-RoughJewel -outputPath "$basePath\rough_jewel.png"

Write-Host "`nJewel textures created successfully!"
Write-Host "These are grayscale textures that will be tinted by Minecraft's color system"
Write-Host "based on the materials used to create each jewel."
