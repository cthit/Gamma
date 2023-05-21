package it.chalmers.gamma.app.authority.domain;


import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.GammaUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RecordBuilder
public record Authority(Client client,
                        AuthorityName name,
                        List<SuperGroupPost> posts,
                        List<SuperGroup> superGroups,
                        List<GammaUser> users) implements AuthorityBuilder.With {

    public Authority {
        Objects.requireNonNull(client);
        Objects.requireNonNull(name);
        Objects.requireNonNull(posts);
        Objects.requireNonNull(superGroups);
        Objects.requireNonNull(users);
    }

    public record SuperGroupPost(SuperGroup superGroup, Post post) {
    }
}