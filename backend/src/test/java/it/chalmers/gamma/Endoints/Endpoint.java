package it.chalmers.gamma.Endoints;

public class Endpoint {
    private final String path;
    private final Method method;
            // Add Permission?
    public Endpoint(String path, Method method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }

}
