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
        return id;
    }

    public String getToken() {
        return token;
    }

    public ITUserDTO getItUserDTO() {
        return itUserDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordResetTokenDTO that = (PasswordResetTokenDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(token, that.token) &&
                Objects.equals(itUserDTO, that.itUserDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, itUserDTO);
    }

    @Override
    public String toString() {
        return "PasswordResetTokenDTO{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", itUserDTO=" + itUserDTO +
                '}';
    }
}
