package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.group.UnofficialPostName;
import it.chalmers.gamma.domain.user.AcceptanceYear;
import it.chalmers.gamma.domain.user.FirstName;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.user.LastName;
import it.chalmers.gamma.domain.user.Name;
import it.chalmers.gamma.domain.user.Nick;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.Cid;

import java.util.List;

public record MockData(List<MockUser> users,
                       List<MockGroup> groups,
                       List<MockSuperGroup> superGroups,
                       List<MockPost> posts,
                       List<MockPostAuthority> postAuthorities) {

    public record MockGroup(GroupId id,
                            Name name,
                            PrettyName prettyName,
                            List<MockMembership> members,
                            SuperGroupId superGroupId) { }

    public record MockMembership(UserId userId,
                                 PostId postId,
                                 UnofficialPostName unofficialPostName) { }


    public record MockPost(PostId id,
                           Text postName) { }

    public record MockSuperGroup(SuperGroupId id,
                                 Name name,
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


