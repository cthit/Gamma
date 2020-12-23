package it.chalmers.gamma.service;

import it.chalmers.gamma.user.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.user.ITUserRepository;
import it.chalmers.gamma.repository.WhitelistRepository;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.user.response.UserNotFoundResponse;
import it.chalmers.gamma.response.whitelist.WhitelistDoesNotExistsException;
import org.springframework.stereotype.Service;

@Service
public class DTOToEntityService {
    private final ITUserRepository itUserRepository;
    private final WhitelistRepository whitelistRepository;

    public DTOToEntityService(ITUserRepository itUserRepository, WhitelistRepository whitelistRepository) {
        this.itUserRepository = itUserRepository;
        this.whitelistRepository = whitelistRepository;
    }


    protected ITUser fromDTO(ITUserDTO itUserDTO) {
        return this.itUserRepository.findById(itUserDTO.getId())
                .orElseThrow(UserNotFoundResponse::new);
    }

    protected Whitelist fromDTO(WhitelistDTO whitelistDTO) {
        return this.whitelistRepository.findById(whitelistDTO.getId())
                .orElseThrow(WhitelistDoesNotExistsException::new);
    }
}
