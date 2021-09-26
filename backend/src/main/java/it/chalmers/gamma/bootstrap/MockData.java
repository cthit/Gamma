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
import java.util.UUID;

public record MockData(List<MockUser> users,
                       List<MockGroup> groups,
                       List<MockSuperGroup> superGroups,
                       List<MockPost> posts,
                       List<MockPostAuthority> postAuthorities) {

    public record MockGroup(UUID id,
                            String name,
                            String prettyName,
                            List<MockMembership> members,
                            UUID superGroupId) { }

    public record MockMembership(UUID userId,
                                 UUID postId,
                                 String unofficialPostName) { }


    public record MockText(String sv, String en) { }

    public record MockPost(UUID id,
                           MockText postName) { }

    public record MockSuperGroup(UUID id,
                                 String name,
                                 String prettyName,
                                 String type,
                                 List<String> authorities) {
    }

    public record MockUser(
            UUID id,
            String cid,
            String nick,
            String firstName,
            String lastName,
            int acceptanceYear,
            List<String> authorities) { }

    public record MockPostAuthority(
            String name,
            UUID superGroupId,
            UUID postId
    ) { }

}


