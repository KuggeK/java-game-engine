package io.github.kuggek.engine.scripting;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader extends URLClassLoader {

    public JarClassLoader(URLClassLoader parent) {
        super(parent.getURLs(), parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addJar(String path) {
        try {
            addURL(new File(path).toURI().toURL());
        } catch (MalformedURLException e) {
            System.out.println("Failed to add jar to classpath: " + path);
        }
    }
}
