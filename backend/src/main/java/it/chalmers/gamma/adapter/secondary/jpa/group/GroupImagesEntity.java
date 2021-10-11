package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.domain.group.GroupId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "group_images_uri")
public class GroupImagesEntity extends MutableEntity<GroupId> {

    @Id
    @Column(name = "group_id")
    protected UUID groupId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "group_id")
    protected GroupEntity group;

    @Column(name = "avatar_uri")
    protected String avatarUri;

    @Column(name = "banner_uri")
    protected String bannerUri;

    protected GroupImagesEntity() { }

    @Override
    protected GroupId domainId() {
        return new GroupId(this.groupId);
    }

}
