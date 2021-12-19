package it.chalmers.gamma.app.supergroup;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupBuilder;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.Name;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

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
        accessGuard.require(isAdmin());

        this.superGroupTypeRepository.add(new SuperGroupType(type));
    }

    public void removeType(String type) throws SuperGroupTypeRepository.SuperGroupTypeNotFoundException, SuperGroupTypeRepository.SuperGroupTypeHasUsagesException {
        accessGuard.require(isAdmin());

        this.superGroupTypeRepository.delete(new SuperGroupType(type));
    }

    public List<String> getAllTypes() {
        accessGuard.require(isAdmin());

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
        accessGuard.require(isAdmin());

        this.superGroupRepository.save(
                new SuperGroup(
                        SuperGroupId.generate(),
                        0,
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
                                   int version,
                                   String name,
                                   String prettyName,
                                   String type,
                                   String email,
                                   String svDescription,
                                   String enDescription) { }

    public void updateSuperGroup(UpdateSuperGroup updateSuperGroup) throws SuperGroupRepository.SuperGroupNotFoundException {
        accessGuard.require(isAdmin());

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
        accessGuard.require(isAdmin());

        this.superGroupRepository.delete(superGroupId);
    }

    public record SuperGroupDTO(UUID id,
                                int version,
                                String name,
                                String prettyName,
                                String type,
                                String email,
                                String svText,
                                String enText) {
        public SuperGroupDTO(SuperGroup superGroup) {
            this(superGroup.id().value(),
                    superGroup.version(),
                    superGroup.name().value(),
                    superGroup.prettyName().value(),
                    superGroup.type().value(),
                    superGroup.email().value(),
                    superGroup.description().sv().value(),
                    superGroup.description().en().value()
            );
        }
    }

    public List<SuperGroupDTO> getAllSuperGroups() {
        accessGuard.require(isAdmin());

        return this.superGroupRepository.getAll()
                .stream()
                .map(SuperGroupDTO::new)
                .toList();
    }

    public List<SuperGroupDTO> getAllSuperGroupsByType(String superGroupType) {
        accessGuard.require(isAdmin());

        return this.superGroupRepository.getAllByType(new SuperGroupType(superGroupType))
                .stream()
                .map(SuperGroupDTO::new)
                .toList();
    }

    public Optional<SuperGroupDTO> get(UUID superGroupId) {
        return this.superGroupRepository.get(new SuperGroupId(superGroupId)).map(SuperGroupDTO::new);
    }

}
