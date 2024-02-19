package it.chalmers.gamma.adapter.secondary.jpa.allowlist;

import it.chalmers.gamma.adapter.secondary.jpa.util.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "g_allow_list")
public class AllowListEntity extends AbstractEntity<String> {

    @Id
    @Column(name = "cid")
    private String cid;

    protected AllowListEntity() {
    }

    protected AllowListEntity(String cid) {
        this.cid = cid;
    }

    @Override
    public String getId() {
        return this.cid;
    }
}
