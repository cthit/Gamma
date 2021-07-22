package it.chalmers.gamma.app;

import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.app.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void addType(SuperGroupType superGroupType) throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        accessGuard.requireIsAdmin();
        this.superGroupTypeRepository.add(superGroupType);
    }

    public void removeType(SuperGroupType superGroupType) throws SuperGroupTypeRepository.SuperGroupTypeNotFoundException, SuperGroupTypeRepository.SuperGroupTypeHasUsagesException {
        accessGuard.requireIsAdmin();
        this.superGroupTypeRepository.delete(superGroupType);
    }

    public List<SuperGroupType> getAllTypes() {
        accessGuard.requireSignedIn();
        return this.superGroupTypeRepository.getAll();
    }

    public void createSuperGroup(SuperGroup superGroup) throws SuperGroupRepository.SuperGroupAlreadyExistsException {
        accessGuard.requireIsAdmin();
        this.superGroupRepository.create(superGroup);
    }

    public void updateSuperGroup(SuperGroup superGroup) throws SuperGroupRepository.SuperGroupNotFoundException {
        accessGuard.requireIsAdmin();
        this.superGroupRepository.save(superGroup);
    }

    public void deleteSuperGroup(SuperGroupId superGroupId) throws SuperGroupRepository.SuperGroupNotFoundException {
        accessGuard.requireIsAdmin();
        this.superGroupRepository.delete(superGroupId);
    }

    public List<SuperGroup> getAllSuperGroups() {
        accessGuard.requireSignedIn();
        return this.superGroupRepository.getAll();
    }

    public List<SuperGroup> getAllSuperGroupsByType(SuperGroupType superGroupType) {
        accessGuard.requireSignedIn();
        return this.superGroupRepository.getAllByType(superGroupType);
    }

    public Optional<SuperGroup> get(SuperGroupId superGroupId) {
        return this.superGroupRepository.get(superGroupId);
    }

}
