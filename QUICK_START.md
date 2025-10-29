# Quick Start Guide - Jewel Charms Mod

Follow these steps in order to build and test your mod.

## Step 1: Check Java Installation

Run this command in PowerShell:

```powershell
.\setup-java.ps1
```

This script will:
- ✓ Check if Java 17 is installed
- ✓ Check if JAVA_HOME is configured
- ✓ Provide installation instructions if needed
- ✓ Offer to set JAVA_HOME for you

### If Java is Not Installed

1. **Download Java 17 (Eclipse Temurin)**:
   - Go to: https://adoptium.net/temurin/releases/?version=17
   - Download the Windows installer (`.msi` file)

2. **Install Java 17**:
   - Run the installer
   - **IMPORTANT**: During installation, check these boxes:
     - ☑ Add to PATH
     - ☑ Set JAVA_HOME variable

3. **Restart PowerShell** after installation

4. **Verify** by running: `java -version`
   - Should show: `openjdk version "17.0.x"`

## Step 2: Download Gradle Wrapper

Run this command:

```powershell
.\setup-gradle.ps1
```

This downloads the `gradle-wrapper.jar` file needed to build the mod.

**If the download fails**, manually download:
- URL: https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar
- Save to: `gradle/wrapper/gradle-wrapper.jar`

## Step 3: Build the Mod

Once Java and Gradle wrapper are set up:

```powershell
.\gradlew.bat build
```

### First Build Notes

- **Takes 5-10 minutes** (downloads dependencies)
- **Requires internet connection**
- **Subsequent builds are much faster** (30-60 seconds)

### Build Output

If successful, you'll see:
```
BUILD SUCCESSFUL in Xs
```

Your mod JAR will be at: `build/libs/jewelcharms-1.0.0.jar`

## Step 4: Test in Development

To launch Minecraft with your mod loaded:

```powershell
.\gradlew.bat runClient
```

This will:
1. Set up a development environment
2. Launch Minecraft 1.20.1 with Forge
3. Load your mod automatically

**Note**: You'll see missing texture warnings (purple/black checkerboard) until you add textures.

## Expected Warnings

When you run the mod, you'll see these warnings (they're normal):

```
[WARN] Missing texture: jewelcharms:textures/block/jewel_creation_station.png
[WARN] Missing texture: jewelcharms:textures/item/jewel.png
[WARN] Missing texture: jewelcharms:textures/item/jewel_socket_template.png
[WARN] Missing texture: jewelcharms:textures/gui/jewel_creation_station.png
```

The mod will still **work**, but visuals will show missing texture patterns.

## What Works Without Textures

You can still test these features:

1. **Give yourself items** via command:
   ```
   /give @p jewelcharms:jewel_creation_station
   /give @p jewelcharms:jewel_socket_template
   ```

2. **Attach jewels** to tools (they'll be invisible but functional)

3. **Test effects** (mining speed, damage, etc. all work)

4. **Check tooltips** (will show jewel info even without textures)

## Common Issues

### "JAVA_HOME is not set"

Run `.\setup-java.ps1` and follow the instructions to set JAVA_HOME.

**Manual Setup:**
1. Find your Java installation (usually `C:\Program Files\Eclipse Adoptium\jdk-17.x.x`)
2. Press Windows key, search "Environment Variables"
3. Click "Environment Variables"
4. Under "System variables", click "New"
5. Variable name: `JAVA_HOME`
6. Variable value: Your Java path (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot`)
7. Click OK
8. **Restart PowerShell**

### "gradle-wrapper.jar not found"

Run `.\setup-gradle.ps1` again, or download manually (see Step 2).

### "Connection timed out" during build

- Check your internet connection
- Gradle is downloading ~200MB of dependencies on first build
- Try again after a few minutes

### Build fails with compilation errors

This usually means:
- Java version is wrong (need exactly 17)
- Source files are missing
- Run `.\gradlew.bat clean` then try again

### "Task ':compileJava' failed"

Check that all `.java` files are present in `src/main/java/com/jewelcharms/`

## Next Steps

Once the mod builds successfully:

1. **Add Textures** (see [TEXTURE_GUIDE.md](TEXTURE_GUIDE.md))
   - Create the 4 required texture files
   - Place them in the correct asset folders
   - Rebuild with `.\gradlew.bat build`

2. **Test All Features** (see [TESTING_CHECKLIST.md](TODO.md#testing-checklist))
   - Jewel creation with minigame
   - Jewel attachment to tools/weapons
   - Effect functionality
   - Context-aware system (tool vs weapon)

3. **Customize** (see [EFFECTS_GUIDE.md](EFFECTS_GUIDE.md))
   - Edit `config/jewelcharms/material_effects.json`
   - Add your own materials
   - Adjust effect strengths
   - Create unique jewel combinations

## Command Reference

```powershell
# Check Java setup
.\setup-java.ps1

# Download Gradle wrapper
.\setup-gradle.ps1

# Build the mod
.\gradlew.bat build

# Clean build files
.\gradlew.bat clean

# Clean and rebuild
.\gradlew.bat clean build

# Run in dev environment
.\gradlew.bat runClient

# Run dedicated server
.\gradlew.bat runServer

# Generate IDE files (IntelliJ)
.\gradlew.bat idea

# Generate IDE files (Eclipse)
.\gradlew.bat eclipse
```

## File Locations

After building:

```
build/
├── libs/
│   └── jewelcharms-1.0.0.jar    ← Your mod!
├── classes/                      ← Compiled .class files
└── resources/                    ← Processed resources

run/                              ← Dev environment saves
└── ...

config/                           ← Config files (generated on first run)
└── jewelcharms/
    └── material_effects.json
```

## Installing Your Mod

1. Build the mod: `.\gradlew.bat build`
2. Find the JAR: `build/libs/jewelcharms-1.0.0.jar`
3. Copy to: `%APPDATA%\.minecraft\mods\`
4. Launch Minecraft 1.20.1 with Forge
5. Enjoy!

## Getting Help

If you're stuck:

1. **Check Java**: `java -version` (should show 17)
2. **Check JAVA_HOME**: `echo $env:JAVA_HOME` (should point to Java 17)
3. **Clean build**: `.\gradlew.bat clean build`
4. **Check logs**: Look at `build/` folder for error details
5. **Read error messages carefully** - they often tell you exactly what's wrong

## Success Checklist

- ☐ Java 17 installed
- ☐ JAVA_HOME set
- ☐ Gradle wrapper downloaded
- ☐ Build succeeds without errors
- ☐ `jewelcharms-1.0.0.jar` exists in `build/libs/`
- ☐ Dev environment launches with `.\gradlew.bat runClient`

Once all checkboxes are complete, you're ready to add textures and fully test the mod!

## What You've Built

At this point, you have a **fully functional** Minecraft mod with:
- ✓ 30+ working effects
- ✓ Context-aware tool/weapon system
- ✓ Jewel creation with minigame
- ✓ Smithing table integration
- ✓ 35 pre-configured materials
- ✓ Complete config system

**Only missing**: Textures for visual polish!

---

**Ready to build?** Start with Step 1 above! 🚀
