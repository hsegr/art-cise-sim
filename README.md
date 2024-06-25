# CISE Sim

[![security status](https://www.meterian.com/badge/pb/5fa62dc3-4eea-4652-8b35-47753ad61b0d/security)](https://www.meterian.com/projects/?id=5fa62dc3-4eea-4652-8b35-47753ad61b0d)
[![stability status](https://www.meterian.com/badge/pb/5fa62dc3-4eea-4652-8b35-47753ad61b0d/stability)](https://www.meterian.com/projects/?id=5fa62dc3-4eea-4652-8b35-47753ad61b0d)
[![licensing status](https://www.meterian.com/badge/pb/5fa62dc3-4eea-4652-8b35-47753ad61b0d/licensing)](https://www.meterian.com/projects/?id=5fa62dc3-4eea-4652-8b35-47753ad61b0d)

**CISE Sim** is an application capable of sending and receiving CISE messages to/from CISE Nodes, adaptors or other CISE Sims. The CISE Sim is conformant to the CISE Service model.

## Functionalities
- send CISE messages using a template 
- receive CISE messages
- validate the CISE messages according to the CISE Data and Service models
- store sent/received messages
- display the message history and the message threads (messages chains) 
- discover CISE services from a CISE Node 

## Endpoints
The CISE Sim exposes the following endpoints:

| Endpoint|Description 
|---|---
|``http://HOST_ADDRESS:8200/``| Web interface (for web browsers) 
|``http://HOST_ADDRESS:8200/api/messages`` | REST interface (to **receive** CISE messages from other adaptors/nodes/CISE Sim)
|``http://HOST_ADDRESS:8200/api/soap/messages``| SOAP interface (to **receive** CISE messages from other adaptors/nodes/CISE Sim)

>
> The CISE Sim can receive CISE messages from the REST and the SOAP endpoints at the same time.
>
> To change the default port (```8200```), please check the `config.yml` file.
>

# Installation

## Requirements
- GNU/Linux OS
- Java 8 or Java 11
- Docker version 19 at least (optional)
- docker-compose version 1.25 at least (optional)

## Software Packages

The CISE Sim is packaged and distributed as:
- a tar.gz archive: cise-sim-``VERSION``-bin.tar.gz
- a Docker container

## TAR.gz Archive 

Untar the cise-sim-``VERSION``-bin.tar.gz in a folder:

```bash
$ mkdir -p /my/installation/path 
$ tar -xvzf cise-sim-VERSION-bin.tar.gz -C /my/installation/path --strip-components=1
```

Set up `$PATH` and `$JAVA_HOME` variables
```bash
$ export PATH=/path/to/java/bin:$PATH
$ export JAVA_HOME=/path/to/java
```

## Docker container
To use docker image, the following version of docker and docker-compose are needed:  

**docker version 19 at least**  
**docker-compose version 1.25 at least**  

The name of the docker image is 

**ec-jrc/cise-sim:latest**

### Install the docker image 
Perform the following steps : 
- Build the project using maven : `mvn clean package`. It will create the following files under the `/target` folder:
  `cise-sim-<version>-bin.tar.gz`
  `cise-sim-<version>-cli.tar.gz`
  `cise-sim-<version>-src.tar.gz`
- In the base folder of the project, run the script : `build_distribution.sh` - it will create `cise-sim-distribution.tar` file which is required for the docker image.
 
This script will do the following :
- Using the `Dockerfile` build the `ec-jrc/cise-sim:latest` image
- Create a tar file `cise-sim-distribution.tar` containing:
  - `cise-sim-<version>-bin.tar.gz` containing the actual cise-simulator application (extract it to use it directly)
  - `docker_cisesim_latest.tar.gz` the portable docker image
  - Sample directories. These directories can be mapped to the running containers so that they can be manipulated outside the docker image/container
    - `conf` directory with sample inputs ( `dummyKeystore.jks`, `config.yml`, `sim.properties` ). 
    - `msghistory` directory which will be mounted to the running containers in order to store the send/received messages 
    - `logs` directory 
    - `templates` directory containing the available templates of messages that can be sent
  - `docker_install.sh` script so that the docker image can be loaded to docker
  - `docker-compose.yml` configuration to use the container with docker-compose
  - `README.md` the current opened file
  - `sim-cli-readme.md`
  
- Extract the files from  cise-sim-distribution.tar` using `tar -xvf cise-sim-distribution.tar`. Please note that you can copy the .tar file in any location/machine on which you want, in order to run the cise-simulator.
- Install the docker image by running the `./docker_install.sh` script - this will execute the containers 

- Update the `conf` directory by adding the proper .jks file (Java Key Store file). The node administrator should create a specific participant for the CISE Sim and provide the corresponding jks file. 
- Update the sim.properties file accordingly 
- Run the docker container.
  There are two ways on which you can run the container:
1. Using the docker-compose. Please **NOTE** that this command runs only in the foreground which means that closing the shell will also close the containers.
   If needed, you can use a window-like utility (such as `screen`) so the process will run in background.

```docker-compose run --service-ports --rm cise-sim```
2. Using the docker run command directly. In this case you have to manually map the ports and the volume directories :
  
```docker run -it -p 8200:8200 -p 8201:8201 --mount src="$(pwd)/conf",target=/srv/cise-simulator/conf,type=bind --mount src="$(pwd)/logs",target=/srv/cise-simulator/logs,type=bind --mount src="$(pwd)/msghistory",target=/srv/cise-simulator/msghistory,type=bind --mount src="$(pwd)/templates",target=/srv/cise-simulator/templates,type=bind ec-jrc/cise-sim:latest```

   Please **NOTE** that if you run the container using the docker run command you can detach the execution by pressing Ctrl+p and Ctrl+q


### Set up Docker volumes

By default the process above will use the directories shown below and extracted through the `cise-sim-distribution.tar`
:

| Folder | Folder in Docker | Description
| --- | --- | ---
| ``conf`` | /srv/cise-simulator/``conf`` | Configuration files and .jks files
| ``logs`` | /srv/cise-simulator/``logs`` | Logs
| ``msghistory`` | /srv/cise-simulator/``msghistory`` | Sent/received CISE messages
| ``templates`` | /srv/cise-simulator/``templates`` | CISE message templates


# Set up the CISE Sim

## Folder structure

```bash
$ ls -l /my/installation/path/
total 128
-rw-r--r-- 1 cise cise 11452 Jul  8 17:24 README.md
drwxr-xr-x 2 cise cise  4096 Jul  6 11:15 conf     # Configuration files and .jks files
drwxr-xr-x 2 cise cise  4096 Jul  6 11:15 docs     # Documentation
drwxr-xr-x 2 cise cise  4096 Jul  6 11:15 lib      # Java libraries
drwxr-xr-x 2 cise cise  4096 Jul  6 11:15 logs     # Logs
drwxr-xr-x 2 cise cise  4096 Jul  6 11:15 msghistory # Sent/received CISE messages
-rwxr-xr-x 1 cise cise  4236 Jul  6 11:15 sim        # Bash script to launch the CISE Sim
drwxr-xr-x 4 cise cise  4096 Jul  6 11:15 templates  # CISE message templates
```

## Configuration files

The ``conf/`` folder contains three configuration files:

- ``sim.properties``
- ``config.yml``
- Java Key Store (JKS) for message signature.

### sim.properties

This file contains the parameters to set up the protocol, the endpoint and the message signature.

| Parameter|Description|Example|
|----------|-----------|-------|
|simulator.name|Simulator name displayed on the CISE Sim web interface. <br> The property is used only to display the system name on the CISE Sim.<br> The property does not affect the functioning.|sim1-nodeAX
|destination.protocol|Protocol used to send messages to the "destination.url". Allowed values: `SOAP`, `REST`|SOAP
|destination.url| URL of the service endpoint where the CISE Sim will send the XML messages.|http://10.10.10.34:8300/api/soap/messages
|templates.messages.directory|Relative path to the folder with the message templates (from the installation directory).| `templates/messages` (Default value)
|signature.keystore.filename|Filename of the Java Key Store (contained in the ``conf/`` directory).|`dummyKeystore.jks`
|signature.keystore.password|Password if the JKS file `signature.keystore.filename`.|12345
|signature.privatekey.alias|Alias for _key pair_ used to  sign the XML messages. The key pair is stored in the in `signature.keystore.filename` |``cisesim-nodeex.nodeex.eucise.ex``
|signature.privatekey.password|Password of the key pair `signature.privatekey.alias`.|12345
|history.repository.directory|Relative path to the folder with the messages sent/received|`msghistory` (Default value)
|history.gui.maxnummsgs|Maximum number of threads displayed in the user interface|10
|proxy.host|IP address of the HTTP Proxy (Optional)|10.10.10.10
|proxy.port|Port number of the HTTP Proxy (Optional)|1234
|discovery.sender.serviceid|Discovery service, Sender ServiceId (Optional)
|discovery.sender.servicetype|Discovery service, Sender ServiceType (Optional)
|discovery.sender.serviceoperation|Discovery service, Sender ServiceOperation (Optional)

Note: Discovery sevice button will be present in the UI, only if all the three parameters discovery.* are presents
#### Example: sim.properties
```properties
#
# CISE Sim
#

# Simulator name displayed on the CISE Sim web interface.
# The property is used only to display the system name on the CISE Sim.
# The property does not affect the functioning.
simulator.name=eu.eucise.ex.cisesim-nodeex

# Protocol used to send messages to the "destination.url".
# Allowed values: SOAP, REST
destination.protocol=SOAP

# URL of the service endpoint where the CISE Sim will send the messages
destination.url=http://localhost:8300/api/soap/messages

# Relative path to the folder with the message templates
templates.messages.directory=templates/messages

# JKS configuration for message signature
signature.keystore.filename=dummyKeystore.jks
signature.keystore.password=dummyPassword
signature.privatekey.alias=cisesim-nodeex.nodeex.eucise.ex
signature.privatekey.password=dummyPassword

# Relative path to the folder with the messages sent/received
history.repository.directory=msghistory
# Maximum number of messages displayed in the web interface
history.gui.numthreads=100

# Proxy configuration
# proxy.host=10.40.X.5
# proxy.port=8888

# Discovery service Sender parameters
# ServiceId of the Sender
discovery.sender.serviceid=

# Service Type of discovery.sender
discovery.sender.servicetype=

# Service Type of discovery.sender
discovery.sender.serviceoperation=

```

---

### config.yml

The CISE Sim uses Dropwizard as application server. The file ``config.yml`` defines the parameters for the application server. For additional information on this file, please check the [Dropwizard manual](https://www.dropwizard.io/en/latest/manual/configuration.html).

```yaml
server:
  # Protocol and port of simulator web interface
  applicationConnectors:
    - type: http
      port: 8200
``` 

While the logging information can be found mostly under the ``logging.loggers`` configuration:
```yaml
logging:
  level: INFO
  loggers:
    "io.dropwizard.bundles.assets": INFO
    "eu.cise.sim.api": INFO
    "org.eclipse.jetty.server.handler": WARN
    "org.eclipse.jetty.setuid": WARN
    "io.dropwizard.server.DefaultServerFactory": WARN
    "io.dropwizard.bundles.assets.ConfiguredAssetsBundle": WARN
```
#### Example: config.yml 
```yaml
#
# CISE Sim - server configuration 
#
# The CISE Sim uses Dropwizard as application server. 
# For more information on this configuration file, please check: 
# https://www.dropwizard.io/en/latest/manual/configuration.html
#

server:
# Port used to receive CISE messages and for the Web interface
  applicationConnectors:
    - type: http
      port: 8200
# Administration port
  adminConnectors:
    - type: http
      port: 8201

# Log setup in Dropwizard
logging:
  level: INFO
  loggers:
     "io.dropwizard.bundles.assets": INFO
     "eu.cise.dispatcher": INFO
     "org.apache.cxf": WARN
     "eu.cise.emulator.api": INFO
     "org.eclipse.jetty.server.handler": WARN
     "org.eclipse.jetty.setuid": WARN
     "io.dropwizard.server.DefaultServerFactory": WARN
     "io.dropwizard.bundles.assets.ConfiguredAssetsBundle": WARN
     "org.wiremock": INFO
  appenders:
    - type: console
      threshold: ALL
      queueSize: 512
      discardingThreshold: 0
      timeZone: UTC
      target: stdout
      logFormat: "%highlight(%.-1level)|%message%n"
    - type: file
      threshold: ALL
      logFormat: "%d{HH:mm:ss}[%5.-5thread]%-5.5level|%-25.25logger{1}|%msg%n"
      currentLogFilename: ./logs/sim.log
      archivedLogFilenamePattern: ./logs/sim-%d{yyyy-MM-dd}.log.gz
      archivedFileCount: 5
      timeZone: UTC
```

--- 

### Java Key Store

The CISE Sim is distributed with a sample Java Key Store file. 

To connect the CISE Sim to a CISE Node, the node administrator should create a specific participant for the CISE Sim. The node administrator must provide the JKS file for the participant with the parameters to access the certificates.

> The new JKS file **must** be stored under the ``conf`` folder and the sim.properties must be updated with the .jks filename and alias along with the respective passwords.


# Launch the CISE Sim
CISE sim binaries are being built running the maven command `mvn clean package` in the base project folder. 
They are included in `/target/cise-sim-<version>-bin.tar.gz` file and can be extracted using `tar -xvf cise-sim-<version>-bin.tar.gz`.


## As a process 

### Start in foreground
To run the application in a foreground process:

```bash
$ cd /my/installation/path
$ ./sim run
```

The application log will be displayed in the standard output.
The CISE Sim will ``stop`` if the terminal session is closed.

### Start in background
To run the application in a background process:

```bash
$ cd /my/installation/path
$ ./sim start
```

Output:
```bash
== CISE sim =====================================
Java path:    /usr/bin/java
Java version: "11.0.7"
=================================================
[ok] sim started at 2020-07-07T17:09:53+02:00
```

With the start command, the CISE Sim will run in background (using nohup) even if the terminal session is closed.   

The application log will be stored in the file ``logs/sim.log``:

```bash
$ cd /my/installation/path
$ tail -f logs/sim.log
```

### Stop a CISE Sim in background
To stop a CISE Sim launched in background:

```bash
$ cd /my/installation/path
$ ./sim stop
```

Output:

```bash
[ok] sim has been stopped
```

### Other commands 
 
```bash
$ cd /my/installation/path
$ ./sim
Usage: sim COMMAND
sim server lifecycle manager (starting, stopping, debugging).
COMMAND
    start       starts the simulator in a detached shell using nohup command.
    run         starts the simulator in foreground.
    stop        stops the simulator running in background.
    restart     restart the simulator running in background.
    debug-start starts the simulator in a detached shell launching the application
                in debug mode (port 9999).
    debug-run   starts the simulator in foreground launching the application
                in debug mode (port 9999).
    status      show the current status the simulator (started or stopped).
    send        send a message from an xml file
```

#### Sending a message from command line
With the command `./sim send 'filename'` is possible to send a message contained in a xml file.
The destination is the same configured for cise-sim and message sent and ack received will be stored in the cise-sim repository directory

# Message History

Sent/Received CISE messages are stored in the `msghistory` folder by default. 

Each message is stored in a single XML file, whose filename follows this pattern:  
```
Timestamp_TypeName_Direction_Uuid
```

|Parameter|Description|
|---|---|
|Timestamp|Timestamp when the message was processed,following the format : yyyyMMdd-HHmmssSSS
|TypeName|Type of the message (i.e. PULLREQUEST, FORWARD, etc.) For the Acknowledge messages, SYNC are the ones received/sent synchronously after the message was sent/received
|Direction|RECV for message received, or SENT for message sent
|Uuid|Unique identifier (UUID)

Examples:  
`20200611-120029798_PULLREQUEST_SENT_31fb100d-dd13-450d-858b-d410a5f2c345`

`20200611-120029808_ACKSYNCH_RECV_184e0b37-bdb0-4efd-b993-ac18abd1f7ec`
