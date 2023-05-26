# Native Memory Agent

Java agent to send native memory metrics to Stats
 
## Build

```shell
mvn clean package
```


## Usage

```shell
wget https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoServer.java
nc -l -u -p 8125
java -javaagent:target/native-memory-agent-1.0-SNAPSHOT.jar=localhost:8125,foo=bar \
  -XX:NativeMemoryTracking=summary \
  EchoServer.java 8080
```



## Acknowledgements

- [The story of a Java 17 native memory leak](https://www.nickebbitt.com/blog/2022/01/26/the-story-of-a-java-17-native-memory-leak/)
