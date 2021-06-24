package it.chalmers.gamma.adapter.bootstrap;

import it.chalmers.gamma.app.domain.AcceptanceYear;
import it.chalmers.gamma.app.domain.FirstName;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.LastName;
import it.chalmers.gamma.app.domain.EntityName;
import it.chalmers.gamma.app.domain.Nick;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.app.domain.SuperGroupType;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.Text;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.Cid;

import java.util.List;

public record MockData(List<MockUser> users,
                       List<MockGroup> groups,
                       List<MockSuperGroup> superGroups,
                       List<MockPost> posts,
                       List<MockPostAuthority> postAuthorities) {

    public record MockGroup(GroupId id,
                            EntityName name,
                            PrettyName prettyName,
                            List<MockMembership> members,
                            SuperGroupId superGroupId) { }

    public record MockMembership(UserId userId,
                                 PostId postId,
                                 String unofficialPostName) { }


    public record MockPost(PostId id,
                           Text postName) { }

    public record MockSuperGroup(SuperGroupId id,
                                 EntityName name,
                                 PrettyName prettyName,
                                 SuperGroupType type,
                                 List<AuthorityLevelName> authorities) {
    }

    public record MockUser(
            UserId id,
            Cid cid,
            Nick nick,
            FirstName firstName,
            LastName lastName,
            AcceptanceYear acceptanceYear,
            List<AuthorityLevelName> authorities) { }

    public record MockPostAuthority(
            AuthorityLevelName name,
            SuperGroupId superGroupId,
            PostId postId
    ) { }

}


