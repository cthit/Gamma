package it.chalmers.gamma.membership;

import it.chalmers.gamma.group.GroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.UserDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipFinder membershipFinder;

    public MembershipService(MembershipRepository membershipRepository, MembershipFinder membershipFinder) {
        this.membershipRepository = membershipRepository;
        this.membershipFinder = membershipFinder;
    }

    /**
     * adds a user to the group.
     *
     * @param group group the user should be added to
     * @param user  which user is added
     * @param post  what post the user has in group
     * @param postname what the unoficial-post name is
     */
    public void addUserToGroup(GroupDTO group, UserDTO user, PostDTO post, String postname) {
        MembershipPK pk = new MembershipPK(
                user.getId(),
                group.getId(),
                post.getId()
        );
        Membership membership = new Membership();
        membership.setId(pk);
        membership.setUnofficialPostName(postname);
        this.membershipRepository.save(membership);
    }



    public void removeUserFromGroup(GroupDTO group, UserDTO user) {
        MembershipDTO membershipDTO = getMembershipByUserAndGroup(user, group);
        this.membershipRepository.delete(this.getMembership(membershipDTO));
    }

    @Transactional
    public void editMembership(MembershipDTO membershipDTO, String unofficialName, PostDTO post) {
        this.removeUserFromGroup(membershipDTO.getFkitGroupDTO(), membershipDTO.getUser());
        this.addUserToGroup(membershipDTO.getFkitGroupDTO(),
                membershipDTO.getUser(), post,
                unofficialName);
        this.membershipRepository.save(this.getMembership(membershipDTO));
    }

    public void removeAllUsersFromGroup(GroupDTO group) {
        List<UserDTO> users = this.getMembershipsInGroup(group).stream()
                .map(MembershipDTO::getUser).collect(Collectors.toList());
        for (UserDTO user : users) {
            this.membershipRepository.delete(
                    this.getMembership(this.getMembershipByUserAndGroup(user, group)));
        }
    }

    public void removeAllMemberships(UserDTO user) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_UserId(user.getId());
        memberships.forEach(this.membershipRepository::delete);
    }

    public boolean groupIsActiveCommittee(GroupDTO group) {
        return group.isActive();
    }

    public boolean isPostUsed(UUID id) {
        return !this.membershipRepository.findAllById_PostId(id).isEmpty();
    }
}
