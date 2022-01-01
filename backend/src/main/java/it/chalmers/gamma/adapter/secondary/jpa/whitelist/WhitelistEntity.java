package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;
import it.chalmers.gamma.app.user.domain.Cid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "whitelist_cid")
public class WhitelistEntity extends SingleImmutableEntity<Cid> {

    @Id
    @Column(name = "cid")
    private String cid;

    protected WhitelistEntity() {}

    protected WhitelistEntity(String cid) {
        this.cid = cid;
    }

    @Override
    protected Cid get() {
        return Cid.valueOf(this.cid);
    }

}
