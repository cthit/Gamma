package it.chalmers.gamma.adapter.secondary.jpa.allowlist;

import it.chalmers.gamma.adapter.secondary.jpa.util.AbstractEntity;
import it.chalmers.gamma.app.user.domain.Cid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "g_allowlist")
public class AllowListEntity extends AbstractEntity<Cid> {

    @Id
    @Column(name = "cid")
    private String cid;

    protected AllowListEntity() {
    }

    protected AllowListEntity(String cid) {
        this.cid = cid;
    }

    @Override
    public Cid getId() {
        return new Cid(this.cid);
    }
}
