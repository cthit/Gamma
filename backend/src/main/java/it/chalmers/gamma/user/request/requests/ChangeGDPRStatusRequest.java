package it.chalmers.gamma.user.request.requests;

import java.util.Objects;

public class ChangeGDPRStatusRequest {
    private boolean gdpr;

    public boolean isGdpr() {
        return this.gdpr;
    }

    public void setGdpr(boolean gdpr) {
        this.gdpr = gdpr;
    }

    @Override
    public String toString() {
        return "ChangeGDPRStatusRequest{"
            + "gdpr=" + this.gdpr
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChangeGDPRStatusRequest that = (ChangeGDPRStatusRequest) o;
        return this.gdpr == that.gdpr;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gdpr);
    }
}
