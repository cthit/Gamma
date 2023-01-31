package it.chalmers.gamma.adapter.secondary.jpa.user.password;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordReset;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset")
public class UserPasswordResetEntity extends ImmutableEntity<UserId> {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "token")
    private String token;

    protected UserPasswordResetEntity() {
    }

    public UserPasswordResetEntity(UUID userId, Instant createdAt, String token) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.token = token;
    }

    @Override
    public UserId getId() {
        return new UserId(this.userId);
    }

    public PasswordReset toDomain() {
        return new PasswordReset(
                this.getId(),
                new PasswordResetToken(this.token),
                this.createdAt
        );
    }

    public String getToken() {
        return this.token;
    }
}
