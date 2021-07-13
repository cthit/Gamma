package it.chalmers.gamma.app.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

@RecordBuilder
public record GroupImages(GroupId groupId,
                          ImageUri avatarUri,
                          ImageUri bannerUri) implements DTO, GroupImagesBuilder.With {

    public GroupImages {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(avatarUri);
        Objects.requireNonNull(bannerUri);
    }

}
