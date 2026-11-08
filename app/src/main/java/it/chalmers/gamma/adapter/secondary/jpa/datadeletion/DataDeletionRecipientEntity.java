package it.chalmers.gamma.adapter.secondary.jpa.datadeletion;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_data_deletion_recipient")
public class DataDeletionRecipientEntity extends ImmutableEntity<String> {

  @Id
  @Column(name = "email")
  private String email;

  protected DataDeletionRecipientEntity() {}

  protected DataDeletionRecipientEntity(String email) {
    this.email = email;
  }

  @Override
  public String getId() {
    return this.email;
  }
}
