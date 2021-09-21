package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.common.ImageUri;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "group_images_uri")
public class GroupImagesEntity extends MutableEntity<GroupId> {

    @Id
    @Column(name = "group_id")
    private UUID id;

    @Column(name = "avatar_uri")
    private String avatarUri;

    @Column(name = "banner_uri")
    private String bannerUri;

    protected GroupImagesEntity() { }

    protected GroupImagesEntity(UUID id) {
        assert (id != null);

        this.id = id;
    }

    @Override
    protected GroupId id() {
        return new GroupId(this.id);
    }

}
