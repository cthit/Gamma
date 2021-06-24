package it.chalmers.gamma.app.userpasswordreset.service;

import it.chalmers.gamma.app.domain.PasswordReset;
import it.chalmers.gamma.app.domain.PasswordResetToken;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.util.entity.ImmutableEntity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "password_reset")
public class PasswordResetEntity extends ImmutableEntity<UserId, PasswordReset> {

    @Embedded
    private PasswordResetToken token;

    @EmbeddedId
    private UserId userId;

    @Column(name = "created_at")
    private Instant createdAt;

    protected PasswordResetEntity() { }

    protected PasswordResetEntity(PasswordResetToken token, UserId userId, Instant createdAt) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    @Override
    protected UserId id() {
        return this.userId;
    }

    @Override
    protected PasswordReset toDTO() {
        return new PasswordReset(
                this.userId,
                this.token,
                this.createdAt
        );
    }
}
