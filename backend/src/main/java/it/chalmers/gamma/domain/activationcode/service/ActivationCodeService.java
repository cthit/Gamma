package it.chalmers.gamma.domain.activationcode.service;

import it.chalmers.gamma.domain.activationcode.data.ActivationCode;
import it.chalmers.gamma.domain.activationcode.data.ActivationCodeDTO;
import it.chalmers.gamma.domain.activationcode.data.ActivationCodeRepository;
import it.chalmers.gamma.domain.activationcode.exception.ActivationCodeNotFoundException;
import it.chalmers.gamma.domain.Cid;

import it.chalmers.gamma.util.TokenUtils;

import it.chalmers.gamma.domain.whitelist.service.WhitelistService;
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
