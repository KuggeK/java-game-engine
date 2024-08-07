# A Simple 3D Java game engine
A simple game engine implemented in Java. This project is mainly a playground for me to try new stuff (technologies, design patterns etc.) and hopefully learn something from it.

This engine began as a way to learn graphics programming through creating my own simple OpenGL-based real-time 3D rendering engine. I just made my bachelor's thesis on game engines though (read it in Finnish [HERE](https://urn.fi/URN:NBN:fi:tuni-202405145803)), so I already wanted to dabble more into that territory, and so I just decided to expand this project into a game engine.
 
## Running the engine
### Maven
[The project at Maven Central](https://central.sonatype.com/artifact/io.github.kuggek/engine)

You can use the engine in your own project easily with Maven by setting this dependency in your pom.xml:
```
<dependency>
    <groupId>io.github.kuggek</groupId>
    <artifactId>engine</artifactId>
    <version>1.0</version>
</dependency>
```
When using the Maven exec plugin to run the program (with `mvn exec:java` from the command line in the project directory), you need to set the MAVEN_OPTS environment variable to "--add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED". If you are running the engine jar, you need to set these same variables on the command line (java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -jar engine-1.0.jar). This is a problem with the JOGL library. A discussion about this can be found [HERE](https://jogamp.org/bugzilla/show_bug.cgi?id=1317). This also applies when using the engine as a library.

## Prominent third party libraries
### JOGL
I am using JOGL (Java Binding for the OpenGL API) for rendering. I am also using some of their support functionalities for windowing and vector math.

### Ode4j
For the physics simulation I am using ode4j (Java port of Open Dynamics Engine). This is for rigid bodies and collision detection.

### SQLite JDBC Driver
For the game engine's asset database I am using SQLite and the Java driver for creating and connecting to it.
