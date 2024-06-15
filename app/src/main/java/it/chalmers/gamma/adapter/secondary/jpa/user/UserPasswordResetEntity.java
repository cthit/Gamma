package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordReset;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "g_password_reset")
public class UserPasswordResetEntity extends ImmutableEntity<UserId> {

  @Id
  @Column(name = "user_id", columnDefinition = "uuid")
  protected UUID userId;

  @Column(name = "token")
  protected String token;

  protected UserPasswordResetEntity() {}

  @Override
  public UserId getId() {
    return new UserId(this.userId);
  }

  public PasswordReset toDomain() {
    return new PasswordReset(this.getId(), new PasswordResetToken(this.token), this.getCreatedAt());
  }

  public String getToken() {
    return this.token;
  }
}
