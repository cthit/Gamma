package it.chalmers.gamma.factories;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockMembershipFactory {

    @Autowired
    private MembershipService membershipService;

    public MembershipDTO generateMembership(PostDTO post, GroupDTO group, UserDTO user) {
        return new MembershipDTO.MembershipDTOBuilder().post(post).group(group).unofficialPostName(GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE)).user(user).build();
    }

    public MembershipDTO saveMembership(MembershipDTO membership) {
        return this.membershipService.addUserToGroup(
                membership.getGroup(),
                membership.getUser(),
                membership.getPost(),
                membership.getUnofficialPostName()
                );
    }

}
