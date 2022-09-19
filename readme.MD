# Automation Server

A highly configurable, Java-based web server to help you automate your home.

### Features:
- Using simple HTTP calls, send IR commands, invoke command-line calls, send emails, forward requests to other servers on your network, or control a carport door*
- Works great with other automation services like IFTTT
- Highly configurable to suit your environment - set up as much or as little as you need!

    *Extra hardware required

## Installation

### Install on your Raspberry Pi
1. Install Java 17 on the host ([SDKMAN!](https://sdkman.io/) is an easy method)
2. Download the latest jar from [releases](https://github.com/valley-fordham/automation-server/releases) to the host
3. Setup your configuration within the config.xml file - a sample to get you started is in the repository

### Install on another Operating System
- Packages are also available with a bundled JDK to suit your target operating system, for immediate deployment

#### Installation Options
- *jar* - for use in environments already running JDK17
- *zip* - these packages also include JDK17.
    
    Zip package options are suitable for environments that do not already have the appropriate JDK installed. Use the package that suits your target operating system
- *from source* - unzip to your host or git clone this repository, and build with JDK17 using `./gradlew shadowJar` or `./gradlew runtime`



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
-c,--configFile <arg>      the location of the config.xml file
-d,--debug                 enable verbose logging for debugging purposes
-p,--port <arg>            sets the port to listen on  eg. 80
-r,--reload                if present, config.xml will be loaded on every
                           request
```

### Supported Request Types
- broadlink
- carport
- command_line
- email
- gpio
- proxy

### Supported Authentication Methods
- static/pre-shared key (time-based soon to come!)

## How to use Automation Server
The Automation Server works by receiving requests (or 'web-hooks'), and validating it with configuration to ensure that
the request is allowed, and then performing the request.

A configuration file is required for Automation Server to operate correctly. The use of the configuration file allows
the following:
- Only requests pre-determined by the user or administrator will be allowed. This will prevent any unauthorised requests
from external sources causing havoc on your hosts and network
- As the required values for given requests stored in the configuration file, this allows the web-hook URL to be kept
reasonably small

Please examine the provided 'config-sample.xml' to see how a complete configuration file should look. This configuration
file will be validated against a schema. Any issues will be reported on start-up. If the '-r' argument is provided at 
application startup, every request will reload the configuration file - helpful for debugging.

A configuration file will require at least one authentication token element and one request type in order for the
automation server to function.

### Authentication Token
#### What is an authentication token?
For any Webhook request, an authentication token is required. This helps prevents unauthorised access if an attacker is
aware that an Authentication Server is listening. At the moment, only a static (ie. pre-shared key) is supported.

Example: Authentication Token Configuration

	<authentication_token>
		<token>myToken123</token>
		<behaviour>static</behaviour>
	</authentication_token>

- token: the pre-shared key which needs to be provided within any request
- behaviour: the type of authentication to be performed. Currently, only 'static' is accepted

### Broadlink
#### What is Broadlink, and the Broadlink Request Type?
Broadlink is a manufacturer of devices which can be used as IR (infra-red) blasters, a.k.a. universal
remotes. There are a number of open-source APIs on the internet which allow interfacing with these devices without the
use of the manufacturer provided software. When using Automation Server, in order to interface with Broadlink devices
using the 'broadlink' request type, you will need to set up the Broadlink CLI, which can be found on GitHub
[here](https://github.com/mjg59/python-broadlink).

If you're new to the Broadlink CLI, it's recommended you test the above tools independently of Automation Server until
you are comfortable with them.

#### How to configure the 'broadlink' request type
The broadlink element is made up of 4 primary elements:
1. cli_path: Path to the broadlink_cli executable. Full path is recommended
2. device: Represents a physical broadlink device you will interface with
3. signal: A signal code that will be 'blasted' as an IR signal
4. request: A pre-configured external request which links both a device and a signal code

The following is an example of what a basic broadlink configuration could look like:

	<broadlink>
		<cli_path>python3 /home/pi/python-broadlink/cli/broadlink_cli</cli_path>
		<device>
			<name>lounge</name>
			<device_code>0x27c2</device_code>
			<ip_address>10.1.1.1</ip_address>
			<mac_address>250f51770f78</mac_address>
		</device>

		<!-- Aircon -->
		<signal>
			<name>aircon_on</name>
			<code>26004c0272370f2a0f290f0e0f0d0f0e0f29100d0f0d10290f2a0f0d10290f0d100d0e2a1029100d0e2b0f290f0e0e0e0f2a0e0e0f0e0e2a0f0e0f0d0f0e0e0e0f0e0e0e0f0e0e0e100d0f0d100d0e0e0f0e0e0e0f0d100d0f0d0f0e0f0d0f0e0e2a0f0e0e0e100d0e0e100d0e2a10290f0e0e0e0f0e0e0e0f2a0f2a0e0e0f0d0f0e0e0e0f0e0f0d10290f2a0e0e10290f29100d0e0e0f0e0f290f0e0f0d100d0e0e10290f0d10290f2a0f0d0e2b0e0e0f0e0e0e0e2b0e0e100d0e0e0f0d0f0e100d0e0e0f0d0f0e100d0f0d0f0e0f0d100d0f0d0f0e0e0e0f0e0f0d0e0f0f0d0f0e0e0e0f0e0f0d0f0d100d0f0d100d0f2a0e0e100d0e0e0f0e0e0e0e0e0f0e0f0d0f0e0f0d100d0f0d0f0e0e0e0e0e100d0f0e0f0d100d0f2a0e2a100d0f290f2a10290f0d0f00022f75390f290f2a0f0d100d0f0d0f2a0f0d0f0e0f2a0f290f0e0f290f0e100d0f290f2a0f0d0f2a10290f0d100d0f290f0e0f0d0f2a0f0e0f0d0f0e0e0e0f0e0e0e0f0e0e0e0e0e0f0e0e0e100d0e0e0f0e0f0d0f0e0e0e0f0d100d0f0e0e2a100d0f0d100d0f0d0f0e0e2a0f2a0e0e0f0e0e0e100d0e2a0f2a0f0e0e0e0f0d0f0e0e0e100d0e2a0f2a0f0e0e2a0f2a0f0d0f0e0f0d0f2a0f0d0f0e0e0e0f0e0e2a0f0e0e2a0f2a0e0e0f2a0f0d100d0e0e0f2a0e0e0f0d0f0e0f0d100d0f0d100d0f0d100d0f0d100d0f0d100d0f0d0f0e0f0d0f0d0f0e0f0d0f0e0f0d0f0e0f0d0f0e0e0e0f0d0f0e0f0d0f0e0f290f0e0f0d0f0e0e0e0f0e0e0e100d0e0e0f0d0f0e0e0e0f0e0f0d0f0d0f0e0f0d100d0e0e100d0f2a0e2a100d0f2910290f290f0e0f000d05000000000000000000000000</code>
		</signal>
		
		<!-- Lounge Room -->
		<request>
			<name>lounge_aircon_on</name>
			<broadlink_device_name>lounge</broadlink_device_name>
			<signal_name>aircon_on</signal_name>
		</request>
	</broadlink>

##### Device:
- name: Name of the Broadlink device, which the request will reference. This should be unique amongst your devices.
- device_code: Identifying code of the Broadlink device.  Please see the code and documentation
  [here](https://github.com/mjg59/python-broadlink) to determine the appropriate device code.
- ip_address: IP address of the Broadlink device on your network
- mac_address: MAC address of the Broadlink device on your network

##### Signal
- name: Name of the signal code, which the request will reference. This should be unique amongst your signal codes. 
  A separate element to configure your signal codes will be useful in environments where you have more than one
  broadlink device, and where IR-ready devices can respond to the same signal
- code: Signal code to be 'blasted' by the broadlink device. This can be recorded through the use of the Broadlink 
  CLI, which will allow you to listen to other remotes signal codes and write the signal code to the command line

##### Request
- name: The name of the pre-configured request. This name will be referenced in your webhook URL.
- broadlink_device_name: The name of the broadlink device to blast the IR signal from. This should be one of the devices
  configured within your broadlink configuration.
- signal_name: The name of the signal code to blast. This should be one of the signal codes configured within your
  broadlink configuration.
  
##### Example Webhook Request
`localhost/?authentication_token=myToken123&request_type=broadlink&request_name=lounge_aircon_on`

### Carport
#### What is the Carport Request type?
The carport request type is an extension of the GPIO request type. It allows you to create an interface with a carport
door through the use of GPIO commands. It's assumed you have already created your hardware interface with your carport
door. There are a number of internet tutorials which can help you get started with this.

The carport request type leverages the existing GPIO request type to interface with your hardware, but the carport
request adds extra behavioural logic which could not be accomplished with the GPIO request type alone.

#### How to configure the 'carport' request type
The carport element is made up of only 1 primary element, but more than one of these will be required for full
functionality of your carport door interface. One request for each of the four actions is recommended:
- request: A pre-configured external request which links a carport action with a GPIO request name (configured
  separately, see 'GPIO' request type)

The following is an example of what a carport configuration could look like:

	<carport>
		<request>
			<name>close_door</name>
			<action>close</action>
			<gpio_request_name>trigger_door</gpio_request_name>
			<gpio_request_name>read_door_status</gpio_request_name>
			<wait_time>15</wait_time>
		</request>
		<request>
			<name>check_status</name>
			<action>status</action>
			<gpio_request_name>read_door_status</gpio_request_name>
		</request>
		<request>
			<name>open_door</name>
			<action>open</action>
			<gpio_request_name>trigger_door</gpio_request_name>
		</request>
		<request>
			<name>trigger_door</name>
			<action>trigger</action>
			<gpio_request_name>trigger_door</gpio_request_name>
		</request>
	</carport>

##### Request
- name: Name of the pre-configured request. This name will be referenced in your webhook URL.
- action: Action this request should perform. Choices available:
  open (this will check the current status of the door, and open it if it is closed)
  close (this will check the current status of the door, and close it if it is opened)
  trigger (this will send a door 'trigger' event, equivalent to pressing your carport remote button once)
  status (this will return a value based on whether the door is open or closed, usually a 1 or 0)
- gpio_request_name: GPIO request to link the action to. eg. a 'trigger' action should be linked to a GPIO request which
  is equivalent to your carport remote button press
- wait_time: The amount of time to wait in seconds before checking the door status again. This element will only apply
  if the 'close' action has been configured for this request. This should be selected based on the amount of time it
  takes for your carport door to fully open from a closed status.

### Command Line
#### What is the Command Line Request type?
The command line request type allows you to execute any command that could be executed from a command-line. If you are
able to run the command from the Windows Command Prompt or the Linux Terminal, it can be executed with the Automation
Server.

#### How to configure the 'command_line' request type
The carport element is made up of only 1 primary element:
- request: A pre-configured external request which will invoke the configured command

The following is an example of what a command_line configuration could look like. In this example, a CLI tool named
'wakeonlan' has been already installed on the host that Automation Server is running on:

	<command_line>
		<request>
			<name>wake_pc</name>
			<command_line>wakeonlan 1C:87:2C:61:42:21</command_line>
			<output_returned>true</output_returned>
		</request>
	</command_line>

##### Request
- name: Name of the pre-configured request. This name will be referenced in your webhook URL.
- command_line: Command to invoke on the host. This should match exactly with what you would type to invoke the same
  command in your Windows Command Prompt or Linux Terminal.
- output_returned: Determine whether the stdout of the process will be returned to the client as text. Defaults to
  'false' if not provided.

##### Example Webhook Request
`localhost/?authentication_token=myToken123&request_type=command_line&request_name=wake_pc`

### Email
#### What is the Email Request type?
The Email request type allows you to send pre-configured emails to a pre-configured address. While limited within
itself, it can be used in more complicated web applications in order to provide a single point of email configuration
for anything that can invoke webhooks.

#### How to configure the 'email' request type
The email element is made up of 2 primary elements:
- mailbox: Represents a Mail server to send the email with. Mail servers with or without authentication are supported.
- request: A pre-configured external request which will invoke the configured command

The following is an example of what an email configuration could look like:

	<email>
		<mailbox>
			<name>gmail</name>
			<host>smtp.gmail.com</host>
			<port>587</port>
			<authenticate>true</authenticate>
			<username>yourGmailLogin</username>
			<password>yourGmailPassword</password>
			<tls>true</tls>
		</mailbox>
		<request>
			<name>pointless_email</name>
			<mailbox_name>gmail</mailbox_name>
			<to>to@mail</to>
			<to>to2@mail</to>
			<from>from@mail</from>
			<subject>Read me!</subject>
			<message>Nothing to see here!</message>
			<html>false</html>
		</request>
	</email>

##### Mailbox
- name: Name of the mailbox, which the request will reference. This should be unique amongst your mailboxes
- host: Fully-qualified domain and hostname of the mail server
- port: Port of the mail server
- authenticate: Whether authentication is required or not, accepts true/false
- username: If authentication is turned on, this username will be used to authenticate
- password: If authentication is turned on, this password will be used to authenticate alongside the username
- tls: When turned on, TLS (Transport Layer Security) will be turned on. Please note, the obsolete SSL option is not
  supported
  
##### Request
- name: Name of the pre-configured request. This name will be referenced in your webhook URL
- mailbox_name: The name of the mailbox to send the email with
- to: Email address to send the email to here. Multiple <to> fields are supported
- from: Email address to send from here. Most users will allow you to spoof any email address in this field
  if authentication is used
- subject: Subject of the email goes here
- message: Body/Message of the email goes here
- html: If set to true, HTML markup will be supported in the 'message' element

##### Example Webhook Request
`localhost/?authentication_token=myToken123&request_type=email&request_name=pointless_email`

### GPIO
#### What is the GPIO Request type?
The GPIO request type allows you to interface with GPIO pins on a Raspberry Pi. This interface supports both reading and
writing to GPIO pins. Automation Server requires the GPIO CLI tool to be installed on the host, and configured on the
PATH environment variable.

#### How to configure the 'gpio' request type
The email element is made up of only 1 primary element:
- request: A pre-configured external request which will invoke the configured command

The following is an example of what a GPIO configuration could look like. Note that this example is designed alongside
the earlier Carport request type example:

	<gpio>
		<request>
			<name>trigger_door</name>
			<carport_only>false</carport_only>
			<write>
				<pin>7</pin>
				<behaviour>write_then_reset</behaviour>
				<value>1</value>
				<wait_time_before_reset>1000</wait_time_before_reset>
			</write>
		</request>
		<request>
			<name>read_door_status</name>
			<carport_only>false</carport_only>
			<read>
				<pin>7</pin>
				<behaviour>read</behaviour>
			</read>
		</request>
	</gpio>

##### Request
- name: Name of the pre-configured request. This name will be referenced in your webhook URL
- carport_only: Whether this GPIO request should only be accessible internally via Carport Request Type calls. Accepts
  true/false
- write: If the write element is present within the GPIO request, then a GPIO write action will be invoked. Please refer
  to the 'write' element documentation for details
- read: If the read element is present within the GPIO request, then a GPIO read action will be invoked. Please refer to
  the 'read' element documentation for details
  
##### Write
- pin: GPIO pin to write to
- behaviour: 'Write' requests can be defined to behave in different ways depending on requirements. Choices available:
    write (this will set the value of the GPIO pin to the value provided in the 'value' element)
    write_then_reset (this will set the value of the GPIO pin to the value provided in the 'value' element, and then
      and then reset it to the previous value once enough time passes. This cooldown is defined within the
      'wait_time_before_reset' element)
- value: the value to write to the GPIO pin, usually 0 or 1
- wait_time_before_reset: the amount of time to wait in milliseconds before resetting the GPIO pin to its original value

##### Read
- pin: GPIO pin to read from
- behaviour: 'Read' requests have the potential to be defined in different ways depending on requirements. At this time,
  only a standard 'read' behaviour is supported:
  read (this will read the value from the GPIO pin AND return it to the client output stream \[eg. display it on the
  screen if the webhook is invoked from a browser])

##### Example Webhook Request
`localhost/?authentication_token=myToken123&request_type=gpio&request_name=trigger_door`

### Proxy
#### What is the Proxy Request type?
The proxy request type allows the Automation Server to receive a request and forward it to another server. This
allows you to configure your primary Automation Server as a proxy server in order to avoid opening up other servers in
your internal network to the wider internet. Automation Server can forward requests to servers that support web-hooks
and only require URL parameters to invoke actions. It does not support forwarding other HTTP content at this time.

Automation Server can also forward requests to other Automation Servers. It is not possible to proxy to a chain of
3 or more automation servers. eg. An automation server can proxy requests to another automation server, but the second
automation server cannot forward that request again

#### How to configure the 'proxy' request type
The email element is made up of 2 primary elements:
- host: A web server to redirect a request to
- request: A pre-configured external request which will forward to a web server

The following is an example of what a proxy configuration could look like:

	<proxy>
		<host>
			<name>aHost</name>
			<fqdn>mydomain.com</fqdn>
			<scheme>http</scheme>
			<port>80</port>
			<connection_timeout>5000</connection_timeout>
			<read_timeout>5000</read_timeout>
		</host>
		<request>
			<name>a_proxy_request</name>
			<host>aHost</host>
			<for_automation_server>true</for_automation_server>
		</request>
		<request>
			<name>another_proxy_request</name>
			<host>aHost</host>
			<for_automation_server>false</for_automation_server>
			<forward_parameter>parameter_one</forward_parameter>
			<forward_parameter>parameter_two</forward_parameter>
		</request>
	</proxy>

##### Host
- name: Name of host, to be referenced by the request
- fqdn: Fully Qualified Domain Name representing the web server which the request will be forwarded to
- scheme: Scheme to be used in the URL, usually http or https
- port: Port that the web server is listening on
- connection_timeout: Amount of time in milliseconds to wait until giving up on making a connection to the web server
- read_timeout: Amount of time in milliseconds to wait until the web server responds to the forwarded request

##### Request
- name: Name of the pre-configured request. This name will be referenced in your webhook URL
- host: Name of the host to forward this request to
- for_automation_server: If the forwarded request is destined for another Automation Server, this should be set to true.
  When set to true, you will also need to supply another trio of URL parameters which will be processed by the second
  Automation Server; 'proxy_authentication_token', 'proxy_request_type' and 'proxy_request_name'. The initial Automation
  Server will remove the 'proxy_' prefixes, and the second Automation Server will process these URL parameters normally
- forward_parameter: Any URL parameter specified within this element will be forwarded to the proxy web server. Multiple
  forward_parameter elements can be supplied
  
**Important note**: When forwarding a proxy request, the 'authentication_token', 'request_type' and 'request_name' URL
parameters will never be forwarded. Only parameters configured as 'forward_parameter' elements will be forwarded, or the
three standard parameters with a 'proxy_' prefix if 'for_automation_server' is set to true for the request.

##### Example Webhook Requests

`localhost/?authentication_token=myToken123&request_type=proxy&request_name=a_proxy_request&proxy_authentication_token=myToken456&proxy_request_type=command_line&proxy_request_name=wake_pc`

`localhost/?authentication_token=myToken123&request_type=proxy&request_name=another_proxy_request&parameter_one=myValue1&parameter_two=myValue2`


## License
[![Creative Commons Licence](https://i.creativecommons.org/l/by-nc/4.0/88x31.png)](http://creativecommons.org/licenses/by-nc/4.0/)
