package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.internal.supergroup.type.service.SuperGroupTypeName;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;
import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.util.domain.Cid;

import java.util.List;

public record MockData(List<MockUser> users,
                       List<MockGroup> groups,
                       List<MockSuperGroup> superGroups,
                       List<MockPost> posts) {

    public record MockGroup(GroupId id,
                            String name,
                            String prettyName,
                            List<MockMembership> members,
                            SuperGroupId superGroupId) { }

    public record MockMembership(UserId userId,
                                 PostId postId,
                                 String unofficialPostName) { }


    public record MockPost(PostId id,
                           TextDTO postName) { }

    public record MockSuperGroup(SuperGroupId id,
                                 String name,
                                 String prettyName,
                                 SuperGroupTypeName type,
                                 List<GroupId> groups) {
    }

    public record MockUser(
            UserId id,
            Cid cid,
            String nick,
            String firstName,
            String lastName,
            int acceptanceYear) { }


}


