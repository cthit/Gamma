package it.chalmers.gamma.Endoints;

public class Endpoint {
    private final String path;
    private final Method method;
    private final Class mockClass;
            // Add Permission?
    public Endpoint(String path, Method method) {
        this.path = path;
        this.method = method;
        this.mockClass = null;
    }

    public Endpoint(String path, Method method, Class c) {
        this.path = path;
        this.method = method;
        this.mockClass = c;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }

    public Class getMockClass() {
        return mockClass;
    }

    @Override
    public String toString() {
        return "Endpoint{"
                + "path='" + this.path + '\''
                + ", method=" + this.method
                + ", mockClass=" + this.mockClass
                + '}';
    }
}
