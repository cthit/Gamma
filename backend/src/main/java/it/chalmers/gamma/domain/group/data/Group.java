package it.chalmers.gamma.domain.group.data;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.BaseEntity;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import java.util.Calendar;

import javax.persistence.*;

@Entity
@Table(name = "fkit_group")
public class Group implements BaseEntity<GroupShallowDTO> {

    @EmbeddedId
    private GroupId id;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name = "name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

    @Column(name = "becomes_active")
    private Calendar becomesActive;

    @Column(name = "becomes_inactive")
    private Calendar becomesInactive;

    @Column(name = "email")
    private Email email;

    @Embedded
    private SuperGroupId superGroupId;

    protected Group() {}

    public Group(GroupShallowDTO g) {
        assert(g.getId() != null);

        this.id = g.getId();

        apply(g);
    }

    public void apply(GroupShallowDTO g) {
        assert(this.id == g.getId());

        this.avatarURL = g.getAvatarURL();
        this.name = g.getName();
        this.prettyName = g.getPrettyName();
        this.becomesActive = g.getBecomesActive();
        this.becomesInactive = g.getBecomesInactive();
        this.email = g.getEmail();
        this.superGroupId = g.getSuperGroupId();
    }

    @Override
    public GroupShallowDTO toDTO() {
        return new GroupShallowDTO(
                this.id,
                this.becomesActive,
                this.becomesInactive,
                this.email,
                this.name,
                this.prettyName,
                this.avatarURL,
                this.superGroupId
        );
    }

    @Override
    public String toString() {
        return "Group{"
                + "id=" + this.id
                + ", avatarURL='" + this.avatarURL + '\''
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", becomesActive=" + this.becomesActive
                + ", becomesInactive=" + this.becomesInactive
                + ", email=" + this.email + '\''
                + ", superGroup='" + this.superGroupId
                + '}';
    }

}