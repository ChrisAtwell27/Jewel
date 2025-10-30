Add-Type -AssemblyName System.Drawing

# Standard Minecraft GUI dimensions
$width = 176
$height = 166

# Colors matching Minecraft's GUI style
$darkGray = [System.Drawing.Color]::FromArgb(255, 139, 139, 139)
$lightGray = [System.Drawing.Color]::FromArgb(255, 198, 198, 198)
$veryDark = [System.Drawing.Color]::FromArgb(255, 55, 55, 55)
$white = [System.Drawing.Color]::FromArgb(255, 255, 255, 255)
$slotBorder = [System.Drawing.Color]::FromArgb(255, 55, 55, 55)
$slotInner = [System.Drawing.Color]::FromArgb(255, 139, 139, 139)

function Draw-Slot {
    param($graphics, $x, $y)

    $slotBorderBrush = New-Object System.Drawing.SolidBrush($slotBorder)
    $slotInnerBrush = New-Object System.Drawing.SolidBrush($slotInner)

    # Outer border (18x18)
    $graphics.FillRectangle($slotBorderBrush, $x, $y, 18, 18)
    # Inner area (16x16)
    $graphics.FillRectangle($slotInnerBrush, $x + 1, $y + 1, 16, 16)

    $slotBorderBrush.Dispose()
    $slotInnerBrush.Dispose()
}

function Draw-PlayerInventory {
    param($graphics, $startY)

    # Hotbar (9 slots)
    for ($i = 0; $i -lt 9; $i++) {
        Draw-Slot $graphics (8 + $i * 18) ($startY + 58)
    }

    # Main inventory (27 slots, 3 rows)
    for ($row = 0; $row -lt 3; $row++) {
        for ($col = 0; $col -lt 9; $col++) {
            Draw-Slot $graphics (8 + $col * 18) ($startY + $row * 18)
        }
    }
}

# === POLISH STATION GUI ===
Write-Host "Creating Polish Station GUI..."
$polishBmp = New-Object System.Drawing.Bitmap($width, $height)
$g = [System.Drawing.Graphics]::FromImage($polishBmp)

# Background
$bgBrush = New-Object System.Drawing.SolidBrush($lightGray)
$g.FillRectangle($bgBrush, 0, 0, $width, $height)

# Top section border (darker)
$darkBrush = New-Object System.Drawing.SolidBrush($darkGray)
$g.FillRectangle($darkBrush, 0, 0, $width, 72)

# Draw slots for polish station
# Input slot (rough jewel)
Draw-Slot $g 80 26

# Output slot (polished jewel)
Draw-Slot $g 80 56

# Player inventory section
Draw-PlayerInventory $g 84

# Border lines
$veryDarkBrush = New-Object System.Drawing.SolidBrush($veryDark)
$whiteBrush = New-Object System.Drawing.SolidBrush($white)

# Top border
$g.FillRectangle($veryDarkBrush, 0, 0, $width, 1)
# Bottom border
$g.FillRectangle($whiteBrush, 0, $height - 1, $width, 1)
# Left border
$g.FillRectangle($veryDarkBrush, 0, 0, 1, $height)
# Right border
$g.FillRectangle($whiteBrush, $width - 1, 0, 1, $height)

# Divider between container and player inventory
$g.FillRectangle($veryDarkBrush, 7, 73, 162, 1)
$g.FillRectangle($whiteBrush, 7, 74, 162, 1)

$polishBmp.Save("d:\_My Projects\Jewel\src\main\resources\assets\jewelcharms\textures\gui\container\polish_station.png", [System.Drawing.Imaging.ImageFormat]::Png)
Write-Host "Polish Station GUI saved!"

$bgBrush.Dispose()
$darkBrush.Dispose()
$veryDarkBrush.Dispose()
$whiteBrush.Dispose()
$g.Dispose()
$polishBmp.Dispose()

# === JEWEL CREATION STATION GUI ===
Write-Host "Creating Jewel Creation Station GUI..."
$creationBmp = New-Object System.Drawing.Bitmap($width, $height)
$g = [System.Drawing.Graphics]::FromImage($creationBmp)

# Background
$bgBrush = New-Object System.Drawing.SolidBrush($lightGray)
$g.FillRectangle($bgBrush, 0, 0, $width, $height)

# Top section border
$darkBrush = New-Object System.Drawing.SolidBrush($darkGray)
$g.FillRectangle($darkBrush, 0, 0, $width, 72)

# Draw slots for creation station
# Material slots (3 in a row)
$startX = 44  # Center 3 slots (44, 66, 88)
for ($i = 0; $i -lt 3; $i++) {
    Draw-Slot $g ($startX + $i * 22) 26
}

# Output slot (rough jewel)
Draw-Slot $g 80 56

# Player inventory section
Draw-PlayerInventory $g 84

# Border lines
$veryDarkBrush = New-Object System.Drawing.SolidBrush($veryDark)
$whiteBrush = New-Object System.Drawing.SolidBrush($white)

# Top border
$g.FillRectangle($veryDarkBrush, 0, 0, $width, 1)
# Bottom border
$g.FillRectangle($whiteBrush, 0, $height - 1, $width, 1)
# Left border
$g.FillRectangle($veryDarkBrush, 0, 0, 1, $height)
# Right border
$g.FillRectangle($whiteBrush, $width - 1, 0, 1, $height)

# Divider
$g.FillRectangle($veryDarkBrush, 7, 73, 162, 1)
$g.FillRectangle($whiteBrush, 7, 74, 162, 1)

$creationBmp.Save("d:\_My Projects\Jewel\src\main\resources\assets\jewelcharms\textures\gui\container\jewel_creation_station.png", [System.Drawing.Imaging.ImageFormat]::Png)
Write-Host "Jewel Creation Station GUI saved!"

$bgBrush.Dispose()
$darkBrush.Dispose()
$veryDarkBrush.Dispose()
$whiteBrush.Dispose()
$g.Dispose()
$creationBmp.Dispose()

Write-Host "`nBoth GUI textures created successfully!"
