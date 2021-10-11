package it.chalmers.gamma.adapter.secondary.jpa.user.password;

import it.chalmers.gamma.app.domain.user.passwordreset.PasswordReset;
import it.chalmers.gamma.app.domain.user.passwordreset.PasswordResetToken;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "password_reset")
public class UserPasswordResetEntity extends ImmutableEntity<UserId> {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "token")
    private String token;

    protected UserPasswordResetEntity() { }

    public UserPasswordResetEntity(UUID userId, Instant createdAt, String token) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.token = token;
    }

    @Override
    protected UserId domainId() {
        return new UserId(this.userId);
    }

    public PasswordReset toDomain() {
        return new PasswordReset(
                this.domainId(),
                new PasswordResetToken(this.token),
                this.createdAt
        );
    }

    public String getToken() {
        return this.token;
    }
}
