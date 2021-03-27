package it.chalmers.gamma.domain.user.data;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.domain.user.UserId;

import java.time.Year;
import java.util.Objects;

public class UserRestrictedDTO {
    private final UserId id;
    private final Cid cid;
    private final String nick;
    private final String firstName;
    private final String lastName;
    private final Year acceptanceYear;

    public UserRestrictedDTO(UserDTO userDTO) {
        this.id = userDTO.getId();
        this.cid = userDTO.getCid();
        this.nick = userDTO.getNick();
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.acceptanceYear = userDTO.getAcceptanceYear();
    }

    public UserId getId() {
        return this.id;
    }

    public Cid getCid() {
        return this.cid;
    }

    public String getNick() {
        return this.nick;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Year getAcceptanceYear() {
        return this.acceptanceYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRestrictedDTO that = (UserRestrictedDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.cid, that.cid)
                && Objects.equals(this.nick, that.nick)
                && Objects.equals(this.firstName, that.firstName)
                && Objects.equals(this.lastName, that.lastName)
                && Objects.equals(this.acceptanceYear, that.acceptanceYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.cid,
                this.nick,
                this.firstName,
                this.lastName,
                this.acceptanceYear
        );
    }

    @Override
    public String toString() {
        return "ITUserRestrictedDTO{"
                + "id=" + this.id
                + ", cid='" + this.cid + '\''
                + ", nick='" + this.nick + '\''
                + ", firstName='" + this.firstName + '\''
                + ", lastName='" + this.lastName + '\''
                + ", acceptanceYear=" + this.acceptanceYear + '\''
                + '}';
    }
}
