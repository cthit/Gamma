package it.chalmers.gamma.internal.supergroup.type.service;

import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuperGroupTypeFinder implements GetAllEntities<SuperGroupType> {

    private final SuperGroupTypeRepository superGroupTypeRepository;

    public SuperGroupTypeFinder(SuperGroupTypeRepository superGroupTypeRepository) {
        this.superGroupTypeRepository = superGroupTypeRepository;
    }

    @Override
    public List<SuperGroupType> getAll() {
        return this.superGroupTypeRepository.findAll()
                .stream()
                .map(SuperGroupTypeEntity::get)
                .collect(Collectors.toList());
    }
}
