package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;

import java.util.List;
import java.util.UUID;

import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"TooManyMethods"})

public class ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;
    private final WhitelistService whitelistService;

    public ActivationCodeService(ActivationCodeRepository activationCodeRepository, WhitelistService whitelistService) {
        this.activationCodeRepository = activationCodeRepository;
        this.whitelistService = whitelistService;
    }

    /**
     * connects and places a whitelisted user and a code in the database.
     *
     * @param whitelistDTO the information regarding the whitelistDTO
     * @param code the code that is associated with a user
     * @return a copy of the ActivationCode object added to the database
     */
    public ActivationCodeDTO saveActivationCode(WhitelistDTO whitelistDTO, String code) {
        String user = whitelistDTO.getCid();
        if (userHasCode(user)) {
            this.activationCodeRepository.delete(
                    this.activationCodeRepository.findByCid_Cid(user));
        }
        Whitelist whitelist = this.whitelistService.getWhitelist(whitelistDTO);
        ActivationCode activationCode = new ActivationCode(cid);
        activationCode.setCode(code);
        this.activationCodeRepository.save(activationCode);
        return activationCode;
    }

    public boolean codeMatches(String code, String user) {
        ActivationCode activationCode = this.activationCodeRepository.findByCid_Cid(user);
        if (activationCode == null) {
            return false;
        }
        return activationCode.getCode().equals(code);
    }

    public boolean userHasCode(String cid) {
        return this.activationCodeRepository.findByCid_Cid(cid) != null;
    }

    // TODO Delete entry after 1 hour or once code has been used. This does not work.
    public void deleteCode(String cid) {
        this.activationCodeRepository.delete(this.activationCodeRepository.findByCid_Cid(cid));
    }

    public void deleteCode(UUID id) {
        this.activationCodeRepository.deleteById(id);
    }

    public boolean codeExists(UUID id) {
        return this.activationCodeRepository.existsById(id);
    }

    public List<ActivationCode> getAllActivationCodes() {
        return this.activationCodeRepository.findAll();
    }

    public ActivationCode getActivationCode(UUID id) {
        return this.activationCodeRepository.findById(id).orElse(null);
    }
}
