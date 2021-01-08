package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.group.FKITGroupDTO;
import it.chalmers.gamma.domain.membership.MembershipDTO;
import it.chalmers.gamma.domain.post.PostDTO;
import it.chalmers.gamma.domain.user.ITUserDTO;
import it.chalmers.gamma.membership.MembershipService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockMembershipFactory {

    @Autowired
    private MembershipService membershipService;

    public MembershipDTO generateMembership(PostDTO post, FKITGroupDTO group, ITUserDTO user) {
        return new MembershipDTO(
                post,
                group,
                GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE),
                user
        );
    }

    public MembershipDTO saveMembership(MembershipDTO membership) {
        return this.membershipService.addUserToGroup(
                membership.getFkitGroupDTO(),
                membership.getUser(),
                membership.getPost(),
                membership.getUnofficialPostName()
                );
    }

}
