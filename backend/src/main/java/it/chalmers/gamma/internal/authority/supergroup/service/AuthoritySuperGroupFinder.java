package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.internal.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthoritySuperGroupFinder implements GetAllEntities<AuthoritySuperGroupDTO> {

    private final AuthoritySuperGroupRepository authoritySuperGroupRepository;
    private final SuperGroupFinder superGroupFinder;

    public AuthoritySuperGroupFinder(AuthoritySuperGroupRepository authoritySuperGroupRepository,
                                     SuperGroupFinder superGroupFinder) {
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
        this.superGroupFinder = superGroupFinder;
    }

    @Override
    public List<AuthoritySuperGroupDTO> getAll() {
        return this.authoritySuperGroupRepository
                .findAll()
                .stream()
                .map(AuthoritySuperGroup::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    private AuthoritySuperGroupDTO fromShallow(AuthoritySuperGroupShallowDTO authoritySuperGroup) {
        try {
            return new AuthoritySuperGroupDTO(
                    this.superGroupFinder.get(authoritySuperGroup.superGroupId()),
                    authoritySuperGroup.authorityLevelName()
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
