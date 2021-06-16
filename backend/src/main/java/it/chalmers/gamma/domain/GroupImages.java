package it.chalmers.gamma.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.util.domain.abstraction.DTO;

@RecordBuilder
public record GroupImages(GroupId groupId,
                          ImageUri avatarUri,
                          ImageUri bannerUri) implements DTO, GroupImagesBuilder.With { }
