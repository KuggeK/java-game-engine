# A Simple 3D Java game engine
A simple game engine implemented in Java. This project is mainly a playground for me to try new stuff (technologies, design patterns etc.) and hopefully learn something from it.

This engine began as a way to learn graphics programming through creating my own simple OpenGL-based 3D rendering engine. I just made my bachelor's thesis on game engines though (read it in Finnish [HERE](https://urn.fi/URN:NBN:fi:tuni-202405145803)), so I already wanted to dabble more into that territory, and so I just decided to expand this project into a game engine.
 
## Prominent third party libraries
### JOGL
I am using JOGL (Java Binding for the OpenGL API) for rendering. I am also using some of their support functionalities for windowing and vector math.

### Ode4j
For the physics simulation I am using ode4j (Java port of Open Dynamics Engine). This is for rigid bodies and collision detection.

## Running the engine
When using the Maven exec plugin to run the program (with `mvn exec:java`), you need to set MAVEN_OPTS to "--add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED". This is a problem with the JOGL library. A discussion about this can be found [HERE](https://jogamp.org/bugzilla/show_bug.cgi?id=1317).
