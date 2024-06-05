# A Real-Time Rendering Engine in Java
A simple real-time rendering engine implemented with Java and OpenGL, using the JOGL library.

This project is mainly for learning more about OpenGL and graphics programming in general. Java was chosen because it is the programming language I have the most experience in, especially when it comes to building larger programs.

This rendering engine is designed to be used with a game engine, and if I have the time, I will try to make a very simple game engine implementation to work with it.

## Running the engine
When using the Maven exec plugin to run the program (with `mvn exec:java`), you need to set MAVEN_OPTS to "--add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED". This is a problem with the JOGL library. A discussion about this can be found [here](https://jogamp.org/bugzilla/show_bug.cgi?id=1317).
