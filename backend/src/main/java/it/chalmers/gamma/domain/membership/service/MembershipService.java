package it.chalmers.gamma.domain.membership.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.UpdateEntity;
import it.chalmers.gamma.domain.membership.data.db.Membership;
import it.chalmers.gamma.domain.membership.data.db.MembershipPK;
import it.chalmers.gamma.domain.membership.data.db.MembershipRepository;
import it.chalmers.gamma.domain.membership.data.dto.MembershipShallowDTO;

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
        Membership membership = this.membershipFinder.getMembershipEntityByUserGroupPost(newEdit.getUserId(), newEdit.getGroupId(), newEdit.getPostId());
        membership.apply(newEdit);
        this.membershipRepository.save(membership);
    }

}
