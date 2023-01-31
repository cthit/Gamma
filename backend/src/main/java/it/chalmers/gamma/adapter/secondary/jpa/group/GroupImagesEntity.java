package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.group.domain.GroupId;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "group_images_uri")
public class GroupImagesEntity extends MutableEntity<GroupId> {

    @Id
    @Column(name = "group_id", columnDefinition = "uuid")
    protected UUID groupId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "group_id")
    protected GroupEntity group;

    @Column(name = "avatar_uri")
    protected String avatarUri;

    @Column(name = "banner_uri")
    protected String bannerUri;

    protected GroupImagesEntity() {
    }

    @Override
    public GroupId getId() {
        return new GroupId(this.groupId);
    }

}
