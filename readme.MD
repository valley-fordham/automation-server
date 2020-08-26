# Automation Server

A highly configurable, Java-based web server to help you automate your home.

### Features:
- Send simple HTTP requests to invoke various functionality
- Works great with other automation services like IFTTT
- Send IR commands, invoke command-line calls, send emails, forward requests to other servers on your network, or control a carport door*
- Highly configurable to suit your environment - set up as much or as little as you need!

    *Extra hardware may be required!

## Installation

### Install on your Raspberry Pi
1. Install Java 11 on the host
2. Download the latest jar from [releases](https://github.com/valley-fordham/automation-server/releases) to the host
3. Setup your configuration within the config.xml file - a sample to get you started is in the repository

### Install on another Operating System
- Packages are also available with a bundled JDK to suit your target operating system, for immediate deployment

#### Installation Options
- *jar* - for use in Raspberry Pi environments already running OpenJDK 11
- *zip* - these packages also include OpenJDK14.
    
    Zip package options are suitable for environments that do not already have the appropriate OpenJDK installed. Use the package that suits your target operating system
- *from source* - unzip to your host or git clone this repository, and build with the OpenJDK11 or OpenJDK14 using `./gradlew shadowJar` or `./gradlew runtime`

    Please note: building your own runtime package using the `runtime` gradle task will only work with OpenJDK14+


## Usage

### Jar only
- Run the jar with the following command: 

    `java -jar automation-server.jar -c config-sample.xml`
    
### Zip Package
- From the root directory run:

    `bin/automation-server -c config-sample.xml` *( `bin/automation-server.bat` for Windows users)*

    For non-windows users, you may need to use `chmod` to mark `automation-server` as an executable

#### Program Arguments
```
- -c,--configFile <arg>      the location of the config.xml file
- -h,--help                  displays the help text you're seeing now
- -p,--port <arg>            sets the port to listen on  eg. 80
- -r,--reload                if present, config.xml will be loaded on every
                            request
- -t,--tempDirPrefix <arg>   sets prefix for Tomcat's temporary directory
                            name  eg. prefix
```

### Making requests

Invoke GET requests through your device or browser.

    `localhost/?authentication_token=yourAuthenticationToken&request_type=command_line&request_name=wake_pc`

### Supported Request Types
- broadlink
- carport
- command_line
- email
- gpio
- proxy

### Supported Authentication Methods
- static (time-based soon to come!)

## License
[![Creative Commons Licence](https://i.creativecommons.org/l/by-nc/4.0/88x31.png)](http://creativecommons.org/licenses/by-nc/4.0/)