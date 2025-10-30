Add-Type -AssemblyName System.Drawing

# Updated GUI dimensions to accommodate removal section
$width = 176
$height = 206  # Increased from 166

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

# === JEWEL CREATION STATION GUI (UPDATED) ===
Write-Host "Creating updated Jewel Creation Station GUI with removal section..."
$creationBmp = New-Object System.Drawing.Bitmap($width, $height)
$g = [System.Drawing.Graphics]::FromImage($creationBmp)

# Background
$bgBrush = New-Object System.Drawing.SolidBrush($lightGray)
$g.FillRectangle($bgBrush, 0, 0, $width, $height)

# Top section border (creation area)
$darkBrush = New-Object System.Drawing.SolidBrush($darkGray)
$g.FillRectangle($darkBrush, 0, 0, $width, 72)

# Middle section border (removal area)
$g.FillRectangle($darkBrush, 0, 75, $width, 37)

# Draw slots for creation section
# Material slots (3 in a row)
$startX = 44  # Center 3 slots (44, 66, 88)
for ($i = 0; $i -lt 3; $i++) {
    Draw-Slot $g ($startX + $i * 22) 26
}

# Output slot (rough jewel)
Draw-Slot $g 80 56

# Draw slots for removal section
# Removal input slot (tool with jewels)
Draw-Slot $g 34 94

# Removal output slots (extracted jewels)
Draw-Slot $g 79 94
Draw-Slot $g 106 94

# Player inventory section
Draw-PlayerInventory $g 124

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

# Divider between creation and removal sections
$g.FillRectangle($veryDarkBrush, 7, 73, 162, 1)
$g.FillRectangle($whiteBrush, 7, 74, 162, 1)

# Divider between removal and player inventory
$g.FillRectangle($veryDarkBrush, 7, 113, 162, 1)
$g.FillRectangle($whiteBrush, 7, 114, 162, 1)

$creationBmp.Save("d:\_My Projects\Jewel\src\main\resources\assets\jewelcharms\textures\gui\container\jewel_creation_station.png", [System.Drawing.Imaging.ImageFormat]::Png)
Write-Host "Updated Jewel Creation Station GUI saved!"

$bgBrush.Dispose()
$darkBrush.Dispose()
$veryDarkBrush.Dispose()
$whiteBrush.Dispose()
$g.Dispose()
$creationBmp.Dispose()

Write-Host "`nGUI texture updated successfully with removal section!"
