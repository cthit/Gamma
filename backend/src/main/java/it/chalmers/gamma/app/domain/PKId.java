package it.chalmers.gamma.app.domain;

public abstract class PKId<S> implements Id<S> {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Id otherId) {
            return getValue().equals(otherId.getValue());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

}
