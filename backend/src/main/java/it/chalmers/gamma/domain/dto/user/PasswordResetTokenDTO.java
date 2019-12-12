package it.chalmers.gamma.domain.dto.user;

import java.util.Objects;
import java.util.UUID;

public class PasswordResetTokenDTO {
    private final UUID id;
    private final String token;
    private final ITUserDTO itUserDTO;

    public PasswordResetTokenDTO(UUID id, String token, ITUserDTO itUserDTO) {
        this.id = id;
        this.token = token;
        this.itUserDTO = itUserDTO;
    }

    public UUID getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public ITUserDTO getItUserDTO() {
        return this.itUserDTO;
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
                && Objects.equals(this.itUserDTO, that.itUserDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.token, this.itUserDTO);
    }

    @Override
    public String toString() {
        return "PasswordResetTokenDTO{"
                + "id=" + this.id
                + ", token='" + this.token + '\''
                + ", itUserDTO=" + this.itUserDTO
                + '}';
    }
}
