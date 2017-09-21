Messages Producer and Consumer
==============

Specification
-------------
In our system, we have just introduced several components that will generate certain log messages (Strings) in large volumes. Now we need another component that will collect these messages (in the same JVM) and persist them to a filesystem.

Constraints:

1. J2SE only, no external libraries.
2. the messages must be written in order of arrival from the components.
3. the component must not delay the originating component and should handle incoming message as quickly as possible (asynchronous from file writes).
4. the component should create new file every 15 minutes (not blocking any processing).
5. the created file should be easily readable by other Java applications (please write in words how to achieve that).
6. we want a simple message logging system - we don't want a java.util.logging or Log4J appender.

Solution:

The producer / consumer design pattern is a pre-designed solution to separate the two main components by placing a queue in the middle, letting the producers and the consumers execute in different threads. It is quite suitable for this situation.

This way the production of tasks to be consumed is absolutely independent from its consumption. This asynchronism breaks the dependency between the producer and the consumer (execution of the task). It is actually a pretty simple design that can add a lot of performance improvement in certain circumstances.

ConcurrentLinkedQueue means no locks are taken (i.e. no synchronized(this) or Lock.lock calls). It will use a CAS - Compare and Swap operation during modifications to see if the head/tail node is still the same as when it started. It's better performance than  using BlockingQueue.

The export file is a text file in .csv format. 

####Compilation

mvn clean install

####Execution

java -jar message.jar start

java -jar message.jar stop

java -jar message.jar restart

It can be configured as a service application easilly.

####Test

mvn test


