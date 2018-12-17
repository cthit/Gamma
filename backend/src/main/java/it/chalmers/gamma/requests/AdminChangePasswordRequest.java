package it.chalmers.gamma.requests;

import java.util.Objects;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class AdminChangePasswordRequest {
    @Min(value = 8, message = "the password must be at least 8 characters long")
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
