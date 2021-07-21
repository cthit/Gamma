package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "ituser_account_locked")
public class UserLockedEntity extends ImmutableEntity<UserId> {

    @EmbeddedId
    private UserId userId;

    protected UserLockedEntity() {

    }

    @Override
    protected UserId id() {
        return this.userId;
    }

}
