# Native Memory Agent

Java agent to send native memory metrics to StatsD using UDP, every 5 seconds. 
 
## Build

```shell
mvn clean package
```

## Release

```shell
/opt/maven/apache-maven-3.9.2/bin/mvn release:prepare
/opt/maven/apache-maven-3.9.2/bin/mvn release:perform -Darguments="-Dmaven.deploy.skip=true"
```

## Usage

```shell
wget https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoServer.java
nc -l -u -p 8125
java -javaagent:target/native-memory-agent-1.0.jar=localhost:8125,foo=bar \
  -XX:NativeMemoryTracking=summary \
  EchoServer.java 8080
```

 * `localhost:8125` is the address for a UDP StatsD server
 * `foo=bar` are tags to be sent along with metrics, delimted by a comma


## Acknowledgements

- [The story of a Java 17 native memory leak](https://www.nickebbitt.com/blog/2022/01/26/the-story-of-a-java-17-native-memory-leak/)
