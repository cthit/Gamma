package it.chalmers.gamma.db.serializers;

public class SerializerValue {

    private boolean enabled;
    private Object value;
    private String name;

    public SerializerValue(boolean enabled, Object value, String name) {
        this.enabled = enabled;
        this.value = value;
        this.name = name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Object getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }
}
