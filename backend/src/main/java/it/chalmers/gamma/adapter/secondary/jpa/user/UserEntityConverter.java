package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityPostJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthoritySuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityUserJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.group.GroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.group.MembershipJpaRepository;
import it.chalmers.gamma.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserEntityConverter {

    private final UserAvatarJpaRepository avatarRepository;
    private final UserGDPRTrainingJpaRepository gdprRepository;
    private final UserLockedJpaRepository lockedRepository;

    private final AuthorityUserJpaRepository authorityUserRepository;
    private final AuthoritySuperGroupJpaRepository authoritySuperGroupRepository;
    private final AuthorityPostJpaRepository authorityPostRepository;

    private final MembershipJpaRepository membershipRepository;

    public UserEntityConverter(UserAvatarJpaRepository avatarRepository,
                               UserGDPRTrainingJpaRepository gdprRepository,
                               UserLockedJpaRepository lockedRepository,
                               AuthorityUserJpaRepository authorityUserRepository,
                               AuthoritySuperGroupJpaRepository authoritySuperGroupRepository,
                               AuthorityPostJpaRepository authorityPostRepository,
                               MembershipJpaRepository membershipRepository) {
        this.avatarRepository = avatarRepository;
        this.gdprRepository = gdprRepository;
        this.lockedRepository = lockedRepository;
        this.authorityUserRepository = authorityUserRepository;
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
        this.authorityPostRepository = authorityPostRepository;
        this.membershipRepository = membershipRepository;
    }

    public User toDomain(UserEntity entity) {
        UserEntity.UserBase b = entity.toBaseDomain();
        List<Group> groups = this.membershipRepository.find

        return new User(
                b.userId(),
                b.cid(),
                b.email(),
                b.language(),
                b.nick(),
                b.password(),
                b.firstName(),
                b.lastName(),
                b.userAgreementAccepted(),
                b.acceptanceYear(),
                this.gdprRepository.existsById(b.userId().value()),
                this.lockedRepository.existsById(b.userId().value()),
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

}
