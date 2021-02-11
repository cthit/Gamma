package it.chalmers.gamma.domain.passwordreset.data;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "password_reset_token")
@SuppressWarnings("PMD.ExcessiveParameterList")
public class PasswordResetToken {
    @Id
    private UUID id;

    @Column(name = "token")
    private String token;

    @JoinColumn(name = "ituser")
    private UUID userId;

    public PasswordResetToken() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID user) {
        this.userId = user;
    }

    @Override
    public String toString() {
        return "PasswordResetToken{"
            + "id=" + this.id
            + ", token='" + this.token + '\''
            + ", itUser=" + this.userId
            + '}';
    }

    public PasswordResetTokenDTO toDTO() {
        return new PasswordResetTokenDTO(this.id, this.token, this.userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordResetToken that = (PasswordResetToken) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.token, that.token)
            && Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.token, this.userId);
    }
}
