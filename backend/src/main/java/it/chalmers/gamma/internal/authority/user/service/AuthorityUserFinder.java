package it.chalmers.gamma.internal.authority.user.service;

import it.chalmers.gamma.internal.user.service.UserFinder;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorityUserFinder implements GetAllEntities<AuthorityUserDTO> {

    private final AuthorityUserRepository authorityUserRepository;
    private final UserFinder userFinder;

    public AuthorityUserFinder(AuthorityUserRepository authorityUserRepository,
                               UserFinder userFinder) {
        this.authorityUserRepository = authorityUserRepository;
        this.userFinder = userFinder;
    }

    @Override
    public List<AuthorityUserDTO> getAll() {
        return this.authorityUserRepository
                .findAll()
                .stream()
                .map(AuthorityUser::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    private AuthorityUserDTO fromShallow(AuthorityUserShallowDTO authorityUserShallowDTO) {
        try {
            return new AuthorityUserDTO(
                    new UserRestrictedDTO(this.userFinder.get(authorityUserShallowDTO.userId())),
                    authorityUserShallowDTO.authorityLevelName()
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
