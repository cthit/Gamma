package it.chalmers.gamma.requests;

import java.util.Objects;
import javax.validation.constraints.NotEmpty;

public class ValidateJwtRequest {
    @NotEmpty(message = "JWT_MUST_BE_PROVIDED")
    private String jwt;

    public String getJwt() {
        return this.jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValidateJwtRequest that = (ValidateJwtRequest) o;
        return this.jwt.equals(that.jwt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.jwt);
    }

    @Override
    public String toString() {
        return "ValidateJwtRequest{"
            + "jwt='" + this.jwt + '\''
            + '}';
    }
}
