package it.chalmers.gamma.internal.supergrouptype.service;

import it.chalmers.gamma.domain.SuperGroupType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuperGroupTypeService {

    private final SuperGroupTypeRepository superGroupTypeRepository;


    public SuperGroupTypeService(SuperGroupTypeRepository superGroupTypeRepository) {
        this.superGroupTypeRepository = superGroupTypeRepository;
    }

    public void create(SuperGroupType name) throws SuperGroupAlreadyExistsException {
        this.superGroupTypeRepository.save(new SuperGroupTypeEntity(name));
    }

    public void delete(SuperGroupType name) throws SuperGroupNotFoundException, SuperGroupHasUsagesException {
        this.superGroupTypeRepository.deleteById(name);
    }

    public List<SuperGroupType> getAll() {
        return this.superGroupTypeRepository.findAll()
                .stream()
                .map(SuperGroupTypeEntity::get)
                .collect(Collectors.toList());
    }

    public static class SuperGroupAlreadyExistsException extends Exception { }
    public static class SuperGroupNotFoundException extends Exception { }
    public static class SuperGroupHasUsagesException extends Exception { }

}
