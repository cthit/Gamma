package it.chalmers.gamma.domain.passwordreset.data;

import it.chalmers.gamma.domain.user.UserId;

import java.util.Objects;
import java.util.UUID;

public class PasswordResetTokenDTO {

    private final String token;
    private final UserId userId;

    public PasswordResetTokenDTO(UserId userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordResetTokenDTO that = (PasswordResetTokenDTO) o;
        return Objects.equals(this.token, that.token)
                && Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.token, this.userId);
    }

    @Override
    public String toString() {
        return "PasswordResetTokenDTO{"
                + ", token='" + this.token + '\''
                + ", itUserDTO=" + this.userId
                + '}';
    }
}
