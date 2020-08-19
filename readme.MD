# Automation Server

A highly configurable, Java-based web server to help you automate your home.

### Features:
- Send simple HTTP requests to invoke various functionality
- Works great with other automation services like IFTTT
- Send IR commands*, invoke command-line calls, send emails, forward requests to other servers on your network or control a carport door*
- Highly configurable to suit your environment - turn on as much or as little as you need!

*Extra hardware may be required!


## Install
1. Install Java 11 on the host
2. Download and install the latest [release](https://github.com/valley-fordham/automation-server/releases)
3. Setup your configuration within the config.xml file - a sample to get you started is in the repository


## Usage
1. Run the jar with the following command: 

    `java -jar automation-server.jar -c config-sample.xml`
2. Invoke GET requests through your device or browser.

    `localhost/?authentication_token=yourAuthenticationToken&request_type=command_line&request_name=wake_pc`

## Instructions
###Supported Request Types
- broadlink
- carport
- command_line
- email
- gpio
- proxy

###Supported Authentication Methods
- static (time-based soon to come!)

## License
[![Creative Commons Licence](https://i.creativecommons.org/l/by-nc/4.0/88x31.png)](http://creativecommons.org/licenses/by-nc/4.0/)