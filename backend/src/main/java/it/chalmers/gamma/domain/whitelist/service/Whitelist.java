package it.chalmers.gamma.domain.whitelist.service;

import it.chalmers.gamma.util.domain.Cid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "whitelist")
public class Whitelist {

    @EmbeddedId
    private Cid cid;

    protected Whitelist() {}

    protected Whitelist(Cid cid) {
        this.cid = cid;
    }

    public Cid getCid() {
        return cid;
    }
}
