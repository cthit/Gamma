package it.chalmers.gamma.app.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

@RecordBuilder
public record GroupImages(GroupId groupId,
                          ImageUri avatarUri,
                          ImageUri bannerUri) implements DTO, GroupImagesBuilder.With { }
