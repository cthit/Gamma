package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.Cid;

import java.util.List;

public record MockData(List<MockUser> users,
                       List<MockGroup> groups,
                       List<MockSuperGroup> superGroups,
                       List<MockPost> posts) {

    public record MockGroup(GroupId id,
                            EntityName name,
                            PrettyName prettyName,
                            List<MockMembership> members,
                            SuperGroupId superGroupId) { }

    public record MockMembership(UserId userId,
                                 PostId postId,
                                 String unofficialPostName) { }


    public record MockPost(PostId id,
                           TextDTO postName) { }

    public record MockSuperGroup(SuperGroupId id,
                                 EntityName name,
                                 PrettyName prettyName,
                                 SuperGroupType type,
                                 List<GroupId> groups) {
    }

    public record MockUser(
            UserId id,
            Cid cid,
            Nick nick,
            FirstName firstName,
            LastName lastName,
            int acceptanceYear) { }


}


