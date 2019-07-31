package it.chalmers.gamma.requests;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.http.ResponseEntity;

public class DeleteMeRequest {
    @NotNull
    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    private String password;

    public String getPassword() {
        return password;
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
