package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;

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
