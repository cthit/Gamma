package it.chalmers.gamma.activationcode;

import it.chalmers.gamma.activationcode.exception.ActivationCodeNotFoundException;
import it.chalmers.gamma.domain.Cid;

import it.chalmers.gamma.activationcode.response.ActivationCodeDoesNotExistResponse;
import it.chalmers.gamma.util.TokenUtils;
import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;

import it.chalmers.gamma.whitelist.service.WhitelistService;
import org.springframework.stereotype.Service;

@Service
public class ActivationCodeService {

    private final ActivationCodeFinder activationCodeFinder;
    private final WhitelistService whitelistService;
    private final ActivationCodeRepository activationCodeRepository;

    public ActivationCodeService(ActivationCodeFinder activationCodeFinder,
                                 ActivationCodeRepository activationCodeRepository,
                                 WhitelistService whitelistService) {
        this.activationCodeFinder = activationCodeFinder;
        this.activationCodeRepository = activationCodeRepository;
        this.whitelistService = whitelistService;
    }

    public ActivationCodeDTO saveActivationCode(Cid cid) {
        tryDeleteCode(cid);

        String code = TokenUtils.generateToken(8, TokenUtils.CharacterTypes.NUMBERS);
        return this.activationCodeFinder.toDTO(this.activationCodeRepository.save(new ActivationCode(cid, code)));
    }

    public void tryDeleteCode(Cid cid) {
        try {
            deleteCode(cid);
        } catch (ActivationCodeNotFoundException ignored) { }
    }

    public void deleteCode(Cid cid) throws ActivationCodeNotFoundException {
        if(!this.activationCodeFinder.cidHasCode(cid)) {
            throw new ActivationCodeNotFoundException();
        }

        this.activationCodeRepository.deleteById(cid.value);
    }

}
