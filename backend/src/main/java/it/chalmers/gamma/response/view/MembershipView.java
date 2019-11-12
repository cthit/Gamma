package it.chalmers.gamma.response.view;

import it.chalmers.gamma.db.entity.Post;

import java.time.Year;
import java.util.Objects;
import java.util.UUID;

public class MembershipView {
    private final Post post;
    private final String unofficialPostName;
    private final String nick;
    private final String firstName;
    private final String lastName;
    private final UUID id;
    private final String cid;
    private final Year acceptanceYear;

    public MembershipView(Post post, String unofficialPostName, String nick, String firstName, String lastName, UUID id, String cid, Year acceptanceYear) {
        this.post = post;
        this.unofficialPostName = unofficialPostName;
        this.nick = nick;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.cid = cid;
        this.acceptanceYear = acceptanceYear;
    }

    public Post getPost() {
        return this.post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public String getNick() {
        return nick;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UUID getId() {
        return id;
    }

    public String getCid() {
        return cid;
    }

    public Year getAcceptanceYear() {
        return acceptanceYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MembershipView that = (MembershipView) o;
        return Objects.equals(this.post, that.post)
                && Objects.equals(this.unofficialPostName, that.unofficialPostName)
                && Objects.equals(this.nick, that.nick)
                && Objects.equals(this.firstName, that.firstName)
                && Objects.equals(this.lastName, that.lastName)
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.post,
                this.unofficialPostName,
                this.nick,
                this.firstName,
                this.lastName,
                this.id,
                this.cid);

    }

    @Override
    public String toString() {
        return "MembershipView{"
                + "post=" + this.post
                + ", unofficialPostName='" + this.unofficialPostName + '\''
                + ", nick='" + this.nick + '\''
                + ", firstName='" + this.firstName + '\''
                + ", lastName='" + this.lastName + '\''
                + ", id=" + this.id
                + ", cid='" + this.cid + '\''
                + '}';
    }
}
