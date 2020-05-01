package it.chalmers.gamma.endoints;

public class JSONParameter {
    private final String key;
    private final String value;

    public JSONParameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "JSONParameter{"
                + "key='" + this.key + '\''
                + ", value='" + this.value + '\''
                + '}';
    }
}
