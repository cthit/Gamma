package it.chalmers.gamma.app.whitelist.service;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.util.entity.SingleImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "whitelist_cid")
public class WhitelistEntity extends SingleImmutableEntity<Cid> {

    @EmbeddedId
    private Cid cid;

    protected WhitelistEntity() {}

    protected WhitelistEntity(Cid cid) {
        this.cid = cid;
    }

    @Override
    protected Cid get() {
        return this.cid;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
