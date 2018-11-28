package it.chalmers.gamma.requests;

import java.util.Objects;

public class ResetPasswordRequest {
    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResetPasswordRequest that = (ResetPasswordRequest) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "ResetPasswordRequest{"
            + "id='" + this.id + '\''
            + '}';
    }
}
