package it.chalmers.gamma.db.serializers;

public class SerializerValue {
    public boolean isEnabled() {
        return enabled;
    }

    private boolean enabled;
        private Object value;
        private String name;

        public SerializerValue(boolean enabled, Object value, String name) {
            this.enabled = enabled;
            this.value = value;
            this.name = name;
        }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
