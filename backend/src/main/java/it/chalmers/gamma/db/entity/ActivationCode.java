package it.chalmers.gamma.db.entity;

import org.springframework.security.oauth2.provider.endpoint.WhitelabelErrorEndpoint;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "activation_code")
public class ActivationCode {

    @Id
    @Column(updatable = false)
    private UUID id;



    @JoinColumn(name = "cid", insertable = true, updatable = false, unique = true)
    @OneToOne(fetch = FetchType.EAGER)
    private Whitelist cid;    // Has a foreign key referencing the Whitelist ID

    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "created_at")
    private Instant createdAt;

    public void setCid(Whitelist cid) {
        this.cid = cid;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    protected ActivationCode(){
        id = UUID.randomUUID();
    }
    public ActivationCode(Whitelist cid){
        id = UUID.randomUUID();
        createdAt = Instant.now();
        this.cid = cid;
    }

    public UUID getId() {

        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCid() {
        return cid.getCid();
    }

    public Whitelist getWhitelist(){
        return cid;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setWhitelist(Whitelist cid){
        this.cid = cid;
    }

    public void setCid(String cid) {
        this.cid.setCid(cid);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivationCode that = (ActivationCode) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(cid, that.cid) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cid, code);
    }

    @Override
    public String toString() {
        return "ActivationCode{" +
                "id=" + id +
                ", whitelistedCid=" + cid +
                ", code='" + code + '\'' +
                '}';
    }
}
