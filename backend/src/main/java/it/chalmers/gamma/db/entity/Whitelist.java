package it.chalmers.delta.db.entity;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "whitelist")
public class Whitelist {
    @Id
    @Column(updatable = false, unique = true)
    private UUID id;

    @Column(name = "cid", unique = true, length = 10, nullable = false)
    private String cid;

    protected Whitelist() {
        this.id = UUID.randomUUID();
    }

    public Whitelist(String cid) {
        this.id = UUID.randomUUID();
        this.cid = cid;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "Whitelist{"
            + "id=" + this.id
            + ", cid='" + this.cid + '\''
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Whitelist whitelist = (Whitelist) o;
        return Objects.equals(this.id, whitelist.id)
            && Objects.equals(this.cid, whitelist.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.cid);
    }
}
