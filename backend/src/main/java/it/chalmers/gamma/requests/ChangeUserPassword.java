package it.chalmers.gamma.requests;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangeUserPassword {

    @Size(min = 8, message = "OLD_PASSWORD_TOO_SHORT")
    @NotNull
    private String oldPassword;

    @Size(min = 8, message = "NEW_PASSWORD_TOO_SHORT")
    @NotNull
    private String newPassword;

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
                && Objects.equals(this.newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.oldPassword, this.newPassword);
    }

    @Override
    public String toString() {
        return "ChangeUserPassword{"
                + "oldPassword='" + this.oldPassword + '\''
                + ", newPassword='" + this.newPassword + '\''
                + '}';
    }
}
