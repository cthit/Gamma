package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "whitelist")
public class Whitelist {
    @Id
    @Column(updatable = false, unique = true)
    private UUID id;

    @Column(name = "cid", unique = true, length = 10, nullable = false)
    private String cid;

    protected Whitelist(){}

    public Whitelist(String cid){
        id = UUID.randomUUID();
        this.cid = cid;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "Whitelist{" +
                "id=" + id +
                ", cid='" + cid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Whitelist whitelist = (Whitelist) o;
        return Objects.equals(id, whitelist.id) &&
                Objects.equals(cid, whitelist.cid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, cid);
    }
}
