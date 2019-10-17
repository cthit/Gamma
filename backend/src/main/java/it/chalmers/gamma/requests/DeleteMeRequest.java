package it.chalmers.delta.requests;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DeleteMeRequest {
    @NotNull(message = "PASSWORD_MUST_NOT_BE_NULL")
    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
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
        DeleteMeRequest that = (DeleteMeRequest) o;
        return Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.password);
    }

    @Override
    public String toString() {
        return "DeleteMeRequest{"
                + "password='" + this.password + '\''
                + '}';
    }
}
