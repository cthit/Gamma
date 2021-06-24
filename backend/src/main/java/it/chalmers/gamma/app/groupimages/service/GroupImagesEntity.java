package it.chalmers.gamma.app.groupimages.service;

import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.GroupImages;
import it.chalmers.gamma.app.domain.ImageUri;
import it.chalmers.gamma.util.entity.MutableEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "group_images_uri")
public class GroupImagesEntity extends MutableEntity<GroupId, GroupImages> {

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

    @Override
    protected GroupImages toDTO() {
        return new GroupImages(
                this.id,
                this.avatarUri,
                this.bannerUri
        );
    }

    @Override
    protected void apply(GroupImages groupImages) {
        assert (this.id == groupImages.groupId());

        this.avatarUri = groupImages.avatarUri();
        this.bannerUri = groupImages.bannerUri();
    }
}
