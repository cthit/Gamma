package it.chalmers.gamma.db.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "activation_code")
public class ActivationCode {

    @Id
    @Column(updatable = false)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "whitelistCid")
    private Whitelist whitelisted;    // Has a foreign key referencing the Whitelist ID

    @Column(name = "code", length = 30)
    private String code;


    public ActivationCode(){
        id = UUID.randomUUID();
    }
    public ActivationCode(UUID id){
        this.id = id;
    }
    public UUID getId() {

        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Whitelist getWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(Whitelist whitelisted) {
        this.whitelisted = whitelisted;
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
                Objects.equals(whitelisted, that.whitelisted) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, whitelisted, code);
    }
}
