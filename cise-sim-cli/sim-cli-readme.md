# CISE Sim Command Line (CLI)

**CISE Sim CLI** is an application capable of sending and receiving CISE messages to/from CISE Nodes, adaptors or other CISE Sims. The CISE Sim CLI is conformant to the CISE Service model.

## Functionalities
- send CISE messages using a template 
- receive CISE messages
- validate the CISE messages according to the CISE Data and Service models.
- store sent/received messages


## Usage:
 ```
java -jar cise-sim-cli.jar <command line parmeters>
```
NOTE:
Open JDK  version: OpenJDK Runtime Environment (build 11.0.17+8-post-Ubuntu-1ubuntu222.04) requires some extra arguments to avoid issues with XPATH. When using this version please update the above command by adding:
 ```
java -Djdk.xml.xpathExprGrpLimit=0 -Djdk.xml.xpathExprOpLimit=0 -Djdk.xml.xpathTotalOpLimit=0 -jar cise-sim-cli.jar <command line parmeters>
```

## Command Line parameters
| Options              | Description                                                                                            | Default                              |
|----------------------|--------------------------------------------------------------------------------------------------------|--------------------------------------|
| --config, -c         | path to the config directory (should contain the keystore and sim.properties files)                    | .                                    |
| --correlation-id, -r | Overrides the correlation id of the message to be sent                                                 ||     |
| --file, -f           | File of the CISE message to be sent                                                                    ||     |
| --help, -h           | Show help (this information)                                                                           ||     |
| --listen, -l         | Set this argument to receive messages. The default port is 9999. To specify the port use the -p option | false                                |
| --message-id, -i     | Overrides the message id of the message to be sent                                                     | f2b707e5-583a-460d-90cb-cef70891dc66 |
| --port, -p           | Port to listen to (to use along with -l option)                                                        | 9999                                 |
| --requires-ack, -a   | Set to true the requiredAck field in the XML of the message                                            | false                                |
| --async, -asy        | Send in a asynchronously mode the indicate number of messages                                          |                                |
| --sync , -sy         | Send in a synchronously mode the indicate number of messages                                           |                                 |    
| --context,-ctx       | Context path to accept mesaages (use with -l option)                                                   | /api/messages                        |

## Example Calls
```bash
java -Djdk.xml.xpathExprGrpLimit=0 -Djdk.xml.xpathExprOpLimit=0 -Djdk.xml.xpathTotalOpLimit=0 -jar cise-sim-cli.jar -f testMessage.xml # send on e message
java -Djdk.xml.xpathExprGrpLimit=0 -Djdk.xml.xpathExprOpLimit=0 -Djdk.xml.xpathTotalOpLimit=0 -jar cise-sim-cli.jar -f testMessage.xml -sy 5 # send 5 messages synchronously
java -Djdk.xml.xpathExprGrpLimit=0 -Djdk.xml.xpathExprOpLimit=0 -Djdk.xml.xpathTotalOpLimit=0 -jar cise-sim-cli.jar -f testMessage.xml -asy 5 # send 5 messages asynchronously
java -Djdk.xml.xpathExprGrpLimit=0 -Djdk.xml.xpathExprOpLimit=0 -Djdk.xml.xpathTotalOpLimit=0-jar cise-sim-cli.jar -f testMessage.xml -i <msgID> # send a message with specified messageId
java -Djdk.xml.xpathExprGrpLimit=0 -Djdk.xml.xpathExprOpLimit=0 -Djdk.xml.xpathTotalOpLimit=0-jar cise-sim-cli.jar -f testMessage.xml -sy 5 -r <corrId> # send 5 messages with the same correlationID
java -Djdk.xml.xpathExprGrpLimit=0 -Djdk.xml.xpathExprOpLimit=0 -Djdk.xml.xpathTotalOpLimit=0-jar cise-sim-cli.jar -l -p 8989 # start the app in listen mode on port 8989


*NOTE that the arguments are needed for OpenJDK Runtime Environment (build 11.0.17+8-post-Ubuntu-1ubuntu222.04)
```
