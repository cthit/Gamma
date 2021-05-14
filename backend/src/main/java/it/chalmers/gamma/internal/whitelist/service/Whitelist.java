package it.chalmers.gamma.internal.whitelist.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.SingleImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "whitelist_cid")
public class Whitelist extends SingleImmutableEntity<Cid> {

    @EmbeddedId
    private Cid cid;

    protected Whitelist() {}

    protected Whitelist(Cid cid) {
        this.cid = cid;
    }

    @Override
    protected Cid get() {
        return this.cid;
    }
}
