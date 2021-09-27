package it.chalmers.gamma.app.bootstrap;

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


