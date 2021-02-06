package it.chalmers.gamma.whitelist.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "whitelist")
public class Whitelist {

    @Id
    private String cid;

    public Whitelist(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid.toLowerCase();
    }

}
