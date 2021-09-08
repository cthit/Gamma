package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.common.ImageUri;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "group_images_uri")
public class GroupImagesEntity extends MutableEntity<GroupId> {

    @EmbeddedId
    private GroupId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "avatar_uri"))
    private ImageUri avatarUri;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "banner_uri"))
    private ImageUri bannerUri;

    protected GroupImagesEntity() { }

    protected GroupImagesEntity(GroupId id) {
        assert (id != null);

        this.id = id;
    }

    @Override
    protected GroupId id() {
        return this.id;
    }

}
