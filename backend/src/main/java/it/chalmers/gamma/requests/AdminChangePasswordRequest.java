package it.chalmers.delta.requests;

import java.util.Objects;
import javax.validation.constraints.Size;

public class AdminChangePasswordRequest {
    @Size(min = 8, message = "PASSWORD_MUST_BE_MORE_THAN_8_CHARACTERS")
    private String password;

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminChangePasswordRequest that = (AdminChangePasswordRequest) o;
        return this.password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.password);
    }

    @Override
    public String toString() {
        return "AdminChangePasswordRequest{"
            + "password=<redacted>'" + '\''
            + '}';
    }
}
