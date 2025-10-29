# Building the Jewel Charms Mod

## Prerequisites

1. **Java 17 or higher** must be installed
   - Check with: `java -version`
   - Download from: https://adoptium.net/

2. **PowerShell** (included with Windows)

## Setup Steps

### Step 1: Download Gradle Wrapper JAR

Run this PowerShell command in the project directory:

```powershell
.\setup-gradle.ps1
```

**OR** manually download:
1. Download `gradle-wrapper.jar` from: https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar
2. Place it in: `gradle/wrapper/gradle-wrapper.jar`

### Step 2: Build the Mod

Once the wrapper is set up, run:

```powershell
.\gradlew.bat build
```

**Note:** On Windows, you must use `gradlew.bat` not `./gradlew`

### Step 3: Find Your Mod

The compiled JAR will be in: `build/libs/jewelcharms-1.0.0.jar`

## Running in Development

To test the mod in a development environment:

```powershell
.\gradlew.bat runClient
```

This will launch Minecraft with your mod loaded.

## Troubleshooting

### "Java is not recognized"
- Install Java 17: https://adoptium.net/
- Make sure Java is in your PATH

### "gradlew.bat is not recognized"
- Make sure you're in the project directory
- Use `.\gradlew.bat` not `./gradlew`

### "Could not find gradle-wrapper.jar"
- Run the setup script: `.\setup-gradle.ps1`
- Or download manually (see Step 1)

### First Build is Slow
- The first build downloads dependencies
- Can take 5-10 minutes
- Subsequent builds are much faster

### Build Fails with Errors
- Make sure you have Java 17 (not 8, not 21)
- Delete `.gradle` folder and try again
- Check that all source files are present

## Quick Commands Reference

```powershell
# Setup (first time only)
.\setup-gradle.ps1

# Build the mod
.\gradlew.bat build

# Run in dev environment
.\gradlew.bat runClient

# Clean build files
.\gradlew.bat clean

# Clean and rebuild
.\gradlew.bat clean build
```

## What Gets Built

After a successful build, you'll have:

```
build/
├── libs/
│   └── jewelcharms-1.0.0.jar  ← This is your mod!
├── classes/
├── resources/
└── ...
```

## Installing Your Mod

1. Build the mod (see above)
2. Copy `build/libs/jewelcharms-1.0.0.jar`
3. Paste into your `.minecraft/mods/` folder
4. Launch Minecraft 1.20.1 with Forge
5. Enjoy!

## Need Help?

If you encounter issues:
1. Check Java version: `java -version` (should be 17)
2. Delete `.gradle` folder and try again
3. Make sure all files are present (check TODO.md for missing textures)
4. Check the error message carefully
