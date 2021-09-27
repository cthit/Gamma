package it.chalmers.gamma.adapter.secondary.jpa.whitelist;

import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;

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

    protected WhitelistEntity(Cid cid) {
        this.cid = cid.getValue();
    }

    @Override
    protected Cid get() {
        return Cid.valueOf(this.cid);
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
