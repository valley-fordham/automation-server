### Changes
- N/A

### Installation Options
For more installation and usage help, see [README](https://github.com/valley-fordham/automation-server)

- *jar* - for use in Raspberry Pi environments already running JDK17
- *zip* - these packages also include JDK17.
    
    Zip package options are suitable for environments that do not already have the appropriate OpenJDK installed. Use the package that suits your target operating system
- *from source* - unzip to your host or git clone this repository, and build with JDK17 using `./gradlew shadowJar` or `./gradlew runtime`
