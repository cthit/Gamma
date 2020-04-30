package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;

import it.chalmers.gamma.response.activationcode.ActivationCodeDoesNotExistResponse;
import it.chalmers.gamma.util.TokenUtils;
import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;
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
     * @return a copy of the ActivationCode object added to the database
     */
    public ActivationCodeDTO saveActivationCode(WhitelistDTO whitelistDTO) {
        if (this.activationCodeRepository.existsActivationCodeByCid_Cid(whitelistDTO.getCid())) {
            this.deleteCode(whitelistDTO.getCid());
        }
        Whitelist whitelist = this.whitelistService.getWhitelist(whitelistDTO);
        ActivationCode activationCode = new ActivationCode(whitelist);
        activationCode.setCode(TokenUtils.generateToken(8, TokenUtils.CharacterTypes.NUMBERS));
        this.activationCodeRepository.save(activationCode);
        return activationCode.toDTO();
    }

    public boolean codeMatches(String code, String user) {
        ActivationCode activationCode = this.activationCodeRepository.findByCid_Cid(user)
                .orElse(null);
        if (activationCode == null) {
            return false;
        }
        if (!activationCode.isValid()) {
            deleteCode(activationCode.getId().toString());
            return false;
        }
        return activationCode.getCode().equals(code);
    }

    @Transactional
    public void deleteCode(String id) {
        ActivationCode activationCode = this.getActivationCodeDTO(this.getActivationCodeDTO(id));
        this.activationCodeRepository.delete(activationCode);
    }

    public boolean codeExists(String id) {
        if (UUIDUtil.validUUID(id)) {
            return this.activationCodeRepository.existsById(UUID.fromString(id));
        }
        return this.activationCodeRepository.existsActivationCodeByCid_Cid(id);
    }

    public List<ActivationCodeDTO> getAllActivationCodes() {
        return this.activationCodeRepository.findAll().stream()
                .map(ActivationCode::toDTO).collect(Collectors.toList());
    }

    public ActivationCodeDTO getActivationCodeDTO(String id) {
        if (UUIDUtil.validUUID(id)) {
            return this.activationCodeRepository.findById(UUID.fromString(id))
                    .map(ActivationCode::toDTO).orElse(null);
        } else {
            return this.activationCodeRepository.findByCid_Cid(id)
                    .map(ActivationCode::toDTO).orElseThrow(ActivationCodeDoesNotExistResponse::new);
        }
    }
    protected ActivationCode getActivationCodeDTO(ActivationCodeDTO activationCodeDTO) {
        return this.activationCodeRepository.findById(activationCodeDTO.getId())
                .orElseThrow(ActivationCodeDoesNotExistResponse::new);
    }
}
