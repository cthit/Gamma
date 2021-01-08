package it.chalmers.gamma.requests;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangeUserPassword {

    @Size(min = 8, message = "OLD_PASSWORD_TOO_SHORT")
    @NotNull(message = "OLD_PASSWORD_MUST_NOT_BE_NULL")
    private String oldPassword;

    @Size(min = 8, message = "NEW_PASSWORD_TOO_SHORT")
    @NotNull(message = "PASSWORD_MUST_NOT_BE_NULL")
    private String password;

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

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
        ChangeUserPassword that = (ChangeUserPassword) o;
        return Objects.equals(this.oldPassword, that.oldPassword)
                && Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.oldPassword, this.password);
    }

    @Override
    public String toString() {
        return "ChangeUserPassword{"
                + "oldPassword='" + this.oldPassword + '\''
                + ", password='" + this.password + '\''
                + '}';
    }
}
