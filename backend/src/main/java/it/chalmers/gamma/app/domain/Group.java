package it.chalmers.gamma.app.domain;

public record Group(GroupId id,
                    Email email,
                    Name name,
                    PrettyName prettyName,
                    SuperGroup superGroup,
                    ImageUri avatarUri,
                    ImageUri bannerUri) { }
