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
public class PasswordResetToken {
    @Column(name = "token")
    private String token;

    @Id
    @JoinColumn(name = "ituser")
    private UUID userId;

    public PasswordResetToken() { }

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
            + ", token='" + this.token + '\''
            + ", itUser=" + this.userId
            + '}';
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
        return Objects.equals(this.token, that.token)
            && Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.token, this.userId);
    }
}
