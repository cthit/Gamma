package it.chalmers.gamma.internal.supergroup.type.service;

import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuperGroupTypeFinder implements GetAllEntities<SuperGroupTypeName> {

    private final SuperGroupTypeRepository superGroupTypeRepository;

    public SuperGroupTypeFinder(SuperGroupTypeRepository superGroupTypeRepository) {
        this.superGroupTypeRepository = superGroupTypeRepository;
    }

    @Override
    public List<SuperGroupTypeName> getAll() {
        return this.superGroupTypeRepository.findAll()
                .stream()
                .map(SuperGroupType::get)
                .collect(Collectors.toList());
    }
}
