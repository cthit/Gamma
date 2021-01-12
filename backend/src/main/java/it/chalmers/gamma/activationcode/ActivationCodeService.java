package it.chalmers.gamma.activationcode;

import it.chalmers.gamma.whitelist.Whitelist;
import it.chalmers.gamma.whitelist.WhitelistDTO;

import it.chalmers.gamma.activationcode.response.ActivationCodeDoesNotExistResponse;
import it.chalmers.gamma.util.TokenUtils;
import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;

import it.chalmers.gamma.whitelist.WhitelistService;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"TooManyMethods"})
public class ActivationCodeService {

    private final WhitelistService whitelistService;
    private final ActivationCodeRepository activationCodeRepository;

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
        Whitelist whitelist = this.whitelistService.fromDTO(whitelistDTO);
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
    public boolean deleteCode(String id) {
        try {
            ActivationCode activationCode = this.fromDTO(this.getActivationCodeDTO(id));
            this.activationCodeRepository.delete(activationCode);
            return true;
        } catch (ActivationCodeDoesNotExistResponse e) {
            return false;
        }
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

    private ActivationCode fromDTO(ActivationCodeDTO activationCodeDTO) {
        return this.activationCodeRepository.findById(activationCodeDTO.getId())
                .orElseThrow(ActivationCodeDoesNotExistResponse::new);
    }
}
