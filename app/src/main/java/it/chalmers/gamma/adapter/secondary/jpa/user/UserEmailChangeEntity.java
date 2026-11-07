package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeRepository.EmailChange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "g_email_change")
public class UserEmailChangeEntity extends ImmutableEntity<UUID> {

  @Id
  @Column(name = "user_id", columnDefinition = "uuid")
  protected UUID userId;

  @Column(name = "new_email")
  protected String newEmail;

  @Column(name = "token_hash")
  protected String tokenHash;

  protected UserEmailChangeEntity() {}

  @Override
  public UUID getId() {
    return this.userId;
  }

  public EmailChange toDomain() {
    return new EmailChange(new UserId(this.userId), new Email(this.newEmail));
  }
}
