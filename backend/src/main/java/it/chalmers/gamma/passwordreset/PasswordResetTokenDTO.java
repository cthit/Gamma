package it.chalmers.gamma.passwordreset;

import it.chalmers.gamma.user.ITUserDTO;

import java.util.Objects;
import java.util.UUID;

public class PasswordResetTokenDTO {

    private final UUID id;
    private final String token;
    private final UUID userId;

    public PasswordResetTokenDTO(UUID id, String token, UUID userId) {
        this.id = id;
        this.token = token;
        this.userId = userId;
    }

    public UUID getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
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
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.token, that.token)
                && Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.token, this.userId);
    }

    @Override
    public String toString() {
        return "PasswordResetTokenDTO{"
                + "id=" + this.id
                + ", token='" + this.token + '\''
                + ", itUserDTO=" + this.userId
                + '}';
    }
}
