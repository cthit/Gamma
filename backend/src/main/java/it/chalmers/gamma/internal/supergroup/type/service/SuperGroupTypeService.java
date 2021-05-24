package it.chalmers.gamma.internal.supergroup.type.service;

import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityHasUsagesException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupTypeService implements CreateEntity<SuperGroupType>, DeleteEntity<SuperGroupType> {

    private final SuperGroupTypeRepository superGroupTypeRepository;


    public SuperGroupTypeService(SuperGroupTypeRepository superGroupTypeRepository) {
        this.superGroupTypeRepository = superGroupTypeRepository;
    }


    @Override
    public void create(SuperGroupType name) throws EntityAlreadyExistsException {
        this.superGroupTypeRepository.save(new SuperGroupTypeEntity(name));
    }

    @Override
    public void delete(SuperGroupType name) throws EntityNotFoundException, EntityHasUsagesException {
        this.superGroupTypeRepository.deleteById(name);
    }
}
