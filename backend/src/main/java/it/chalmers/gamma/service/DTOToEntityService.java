package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.repository.ITUserRepository;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import org.springframework.stereotype.Service;

@Service
public class DTOToEntityService {
    private final ITUserRepository repository;

    public DTOToEntityService(ITUserRepository repository) {
        this.repository = repository;
    }

    protected ITUser fromDTO(ITUserDTO itUserDTO) {
        return this.repository.findById(itUserDTO.getId()).orElse(null);
    }
}
