package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.UpdateEntity;

import org.springframework.stereotype.Service;

@Service
public class MembershipService implements CreateEntity<MembershipShallowDTO>, DeleteEntity<MembershipPK>, UpdateEntity<MembershipShallowDTO> {

    private final MembershipRepository membershipRepository;
    private final MembershipFinder membershipFinder;

    public MembershipService(MembershipRepository membershipRepository, MembershipFinder membershipFinder) {
        this.membershipRepository = membershipRepository;
        this.membershipFinder = membershipFinder;
    }

    public void create(MembershipShallowDTO membership) {
        this.membershipRepository.save(new Membership(membership));
    }

    public void delete(MembershipPK membershipPK) throws EntityNotFoundException {
        this.membershipRepository.deleteById(membershipPK);
    }

    public void update(MembershipShallowDTO newEdit) throws EntityNotFoundException {
        Membership membership = this.membershipFinder.getMembershipEntityByUserGroupPost(newEdit.userId(), newEdit.groupId(), newEdit.postId());
        membership.apply(newEdit);
        this.membershipRepository.save(membership);
    }

}
