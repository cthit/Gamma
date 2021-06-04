package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import java.time.Year;

import javax.persistence.*;

@Entity
@Table(name = "ituser")
public class UserEntity extends MutableEntity<UserId, UserDTO> {

    @EmbeddedId
    private UserId id;

    @Embedded
    private Cid cid;

    @Embedded
    private Password password;

    @Embedded
    private Nick nick;

    @Embedded
    private FirstName firstName;

    @Embedded
    private LastName lastName;

    @Embedded
    private Email email;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "user_agreement")
    private boolean userAgreement;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "acceptance_year")
    private int acceptanceYear;

    protected UserEntity() { }

    protected UserEntity(UserDTO user) {
        assert(user.id() != null);
        assert(user.cid() != null);

        this.id = user.id();
        this.cid = user.cid();

        this.apply(user);
    }

    protected UserDTO toDTO() {
        return new UserDTO(
                this.id,
                this.cid,
                this.email,
                this.language,
                this.nick,
                this.firstName,
                this.lastName,
                this.userAgreement,
                Year.of(this.acceptanceYear),
                this.activated
        );
    }

    @Override
    protected UserId id() {
        return this.id;
    }

    @Override
    public void apply(UserDTO u) {
        assert(this.id == u.id());
        assert(this.cid == u.cid());

        this.acceptanceYear = u.acceptanceYear().getValue();
        this.activated = u.activated();
        this.email = u.email();
        this.firstName = u.firstName();
        this.lastName = u.lastName();
        this.language = u.language();
        this.nick = u.nick();
        this.userAgreement = u.userAgreement();
    }

    protected void setPassword(Password password) {
        this.password = password;
    }

    protected Password getPassword() {
        return password;
    }

    protected UserId getId() {
        return id;
    }

    protected Cid getCid() {
        return cid;
    }

}