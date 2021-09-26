package it.chalmers.gamma.app.supergroup;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.supergroup.SuperGroupBuilder;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupTypeRepository;
import it.chalmers.gamma.domain.user.Name;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SuperGroupFacade extends Facade {

    private final SuperGroupRepository superGroupRepository;
    private final SuperGroupTypeRepository superGroupTypeRepository;

    public SuperGroupFacade(AccessGuard accessGuard,
                            SuperGroupRepository superGroupRepository,
                            SuperGroupTypeRepository superGroupTypeRepository) {
        super(accessGuard);
        this.superGroupRepository = superGroupRepository;
        this.superGroupTypeRepository = superGroupTypeRepository;
    }

    public void addType(String type) throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        accessGuard.requireIsAdmin();
        this.superGroupTypeRepository.add(new SuperGroupType(type));
    }

    public void removeType(String type) throws SuperGroupTypeRepository.SuperGroupTypeNotFoundException, SuperGroupTypeRepository.SuperGroupTypeHasUsagesException {
        accessGuard.requireIsAdmin();
        this.superGroupTypeRepository.delete(new SuperGroupType(type));
    }

    public List<String> getAllTypes() {
        accessGuard.requireSignedIn();
        return this.superGroupTypeRepository.getAll()
                .stream()
                .map(SuperGroupType::value)
                .toList();
    }

    public record NewSuperGroup(String name,
                                String prettyName,
                                String superGroupType,
                                String email,
                                String svDescription,
                                String enDescription) { }

    public void createSuperGroup(NewSuperGroup newSuperGroup) throws SuperGroupRepository.SuperGroupAlreadyExistsException {
        accessGuard.requireIsAdmin();
        this.superGroupRepository.create(
                new SuperGroup(
                        SuperGroupId.generate(),
                        new Name(newSuperGroup.name),
                        new PrettyName(newSuperGroup.prettyName),
                        new SuperGroupType(newSuperGroup.superGroupType),
                        new Email(newSuperGroup.email),
                        new Text(
                                newSuperGroup.svDescription,
                                newSuperGroup.enDescription
                        )
                )
        );
    }

    public record UpdateSuperGroup(UUID id,
                                   String name,
                                   String prettyName,
                                   String type,
                                   String email,
                                   String svDescription,
                                   String enDescription) { }

    public void updateSuperGroup(UpdateSuperGroup updateSuperGroup) throws SuperGroupRepository.SuperGroupNotFoundException {
        accessGuard.requireIsAdmin();
        SuperGroup oldSuperGroup = this.superGroupRepository.get(new SuperGroupId(updateSuperGroup.id)).orElseThrow();
        SuperGroup newSuperGroup = SuperGroupBuilder
                .builder(oldSuperGroup)
                .name(new Name(updateSuperGroup.name))
                .prettyName(new PrettyName(updateSuperGroup.prettyName))
                .type(new SuperGroupType(updateSuperGroup.type))
                .email(new Email(updateSuperGroup.email))
                .description(
                        new Text(
                                updateSuperGroup.svDescription,
                                updateSuperGroup.enDescription
                        )
                ).build();

        this.superGroupRepository.save(newSuperGroup);
    }

    public void deleteSuperGroup(SuperGroupId superGroupId) throws SuperGroupRepository.SuperGroupNotFoundException {
        accessGuard.requireIsAdmin();
        this.superGroupRepository.delete(superGroupId);
    }

    public record SuperGroupDTO(UUID id, String name, String prettyName, String type, String email) {
        public SuperGroupDTO(SuperGroup superGroup) {
            this(superGroup.id().value(),
                    superGroup.name().value(),
                    superGroup.prettyName().value(),
                    superGroup.type().value(),
                    superGroup.email().value()
            );
        }
    }

    public List<SuperGroupDTO> getAllSuperGroups() {
        accessGuard.requireSignedIn();
        return this.superGroupRepository.getAll()
                .stream()
                .map(SuperGroupDTO::new)
                .toList();
    }

    public List<SuperGroupDTO> getAllSuperGroupsByType(String superGroupType) {
        accessGuard.requireSignedIn();
        return this.superGroupRepository.getAllByType(new SuperGroupType(superGroupType))
                .stream()
                .map(SuperGroupDTO::new)
                .toList();
    }

    public Optional<SuperGroupDTO> get(UUID superGroupId) {
        return this.superGroupRepository.get(new SuperGroupId(superGroupId)).map(SuperGroupDTO::new);
    }

}
