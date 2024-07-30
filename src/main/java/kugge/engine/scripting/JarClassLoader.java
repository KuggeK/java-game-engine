package kugge.engine.scripting;

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
}
