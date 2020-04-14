package it.chalmers.gamma.Endoints;

public class Endpoint {
    private final String path;
    private final Method method;
    private final Class c;
            // Add Permission?
    public Endpoint(String path, Method method) {
        this.path = path;
        this.method = method;
        this.c = null;
    }

    public Endpoint(String path, Method method, Class c) {
        this.path = path;
        this.method = method;
        this.c = c;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }

    public Class getC() {
        return c;
    }
}
