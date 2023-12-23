package it.chalmers.gamma.app.client.domain.restriction;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.GammaUser;

import java.util.List;

@RecordBuilder
public record ClientRestriction(ClientRestrictionId id, List<SuperGroup> superGroups) {

}
