package it.chalmers.gamma.requests;

import java.util.Objects;

public class ChangeGDPRStatusRequest {
    private boolean gdpr;

    public boolean isGdpr() {
        return gdpr;
    }

    public void setGdpr(boolean gdpr) {
        this.gdpr = gdpr;
    }

    @Override
    public String toString() {
        return "ChangeGDPRStatusRequest{" +
                "gdpr=" + gdpr +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeGDPRStatusRequest that = (ChangeGDPRStatusRequest) o;
        return gdpr == that.gdpr;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gdpr);
    }
}
