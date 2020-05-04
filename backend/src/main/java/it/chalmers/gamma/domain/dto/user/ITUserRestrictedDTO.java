package it.chalmers.gamma.domain.dto.user;

import java.time.Year;
import java.util.Objects;
import java.util.UUID;


public class ITUserRestrictedDTO {
    private final UUID id;
    private final String cid;
    private final String nick;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String avatarUrl;
    private final Year acceptanceYear;

    public ITUserRestrictedDTO(ITUserDTO userDTO) {
        this.id = userDTO.getId();
        this.cid = userDTO.getCid();
        this.nick = userDTO.getNick();
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.email = userDTO.getEmail();
        this.phone = userDTO.getPhone();
        this.avatarUrl = userDTO.getAvatarUrl();
        this.acceptanceYear = userDTO.getAcceptanceYear();
    }

    public UUID getId() {
        return this.id;
    }

    public String getCid() {
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

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
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
        ITUserRestrictedDTO that = (ITUserRestrictedDTO) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.cid, that.cid)
            && Objects.equals(this.nick, that.nick)
            && Objects.equals(this.firstName, that.firstName)
            && Objects.equals(this.lastName, that.lastName)
            && Objects.equals(this.email, that.email)
            && Objects.equals(this.phone, that.phone)
            && Objects.equals(this.avatarUrl, that.avatarUrl)
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
                this.email,
                this.phone,
                this.avatarUrl,
                this.acceptanceYear
        );
    }

    @Override
    public String toString() {
        return "ITUserRestrictedDTO{"
            + "id=" + id
            + ", cid='" + cid + '\''
            + ", nick='" + nick + '\''
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", email='" + email + '\''
            + ", phone='" + phone + '\''
            + ", avatarUrl='" + avatarUrl + '\''
            + ", acceptanceYear=" + acceptanceYear
            + '}';
    }
}
