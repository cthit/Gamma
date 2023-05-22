package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.adapter.secondary.jpa.util.AbstractEntity;
import it.chalmers.gamma.app.user.domain.Cid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "g_allowlist")
public class WhitelistEntity extends AbstractEntity<Cid> {

    @Id
    @Column(name = "cid")
    private String cid;

    protected WhitelistEntity() {
    }

    protected WhitelistEntity(String cid) {
        this.cid = cid;
    }

    @Override
    public Cid getId() {
        return new Cid(this.cid);
    }
}
