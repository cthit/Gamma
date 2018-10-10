package it.chalmers.gamma.db.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
    @Id
    private UUID id;

    @Column(name = "token")
    private String token;

    @JoinColumn(name = "ituser")
    @OneToOne
    private ITUser itUser;


    public PasswordResetToken(){
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ITUser getItUser() {
        return itUser;
    }

    public void setItUser(ITUser itUser) {
        this.itUser = itUser;
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", itUser=" + itUser +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordResetToken that = (PasswordResetToken) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(token, that.token) &&
                Objects.equals(itUser, that.itUser);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, token, itUser);
    }
}
