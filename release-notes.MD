### Changes
- N/A

### Installation Options
For more installation and usage help, see [README](https://github.com/valley-fordham/automation-server)

- *jar* - for use in Raspberry Pi environments already running OpenJDK 11
- *zip* - these packages also include OpenJDK14.
    
    Zip package options are suitable for environments that do not already have the appropriate OpenJDK installed. Use the package that suits your target operating system
- *from source* - unzip to your host or git clone this repository, and build with OpenJDK11 or OpenJDK14 using `./gradlew shadowJar` or `./gradlew runtime`

    Please note: building your own runtime package using the `runtime` gradle task will only work with OpenJDK14+