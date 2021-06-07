package it.chalmers.gamma.internal.user.passwordreset.service;

import it.chalmers.gamma.domain.PasswordResetToken;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetTokenEntity extends ImmutableEntity<UserId, PasswordResetToken> {

    @Column(name = "token")
    private String token;

    @EmbeddedId
    private UserId userId;

    @Column(name = "created_at")
    private Instant createdAt;

    protected PasswordResetTokenEntity() { }

    protected PasswordResetTokenEntity(String token, UserId userId, Instant createdAt) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    @Override
    protected UserId id() {
        return this.userId;
    }

    @Override
    protected PasswordResetToken toDTO() {
        return new PasswordResetToken(
                this.userId,
                this.token,
                this.createdAt
        );
    }
}
