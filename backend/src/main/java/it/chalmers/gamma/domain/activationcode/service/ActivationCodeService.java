package it.chalmers.gamma.domain.activationcode.service;

import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.activationcode.data.db.ActivationCode;
import it.chalmers.gamma.domain.activationcode.data.dto.ActivationCodeDTO;
import it.chalmers.gamma.domain.activationcode.data.db.ActivationCodeRepository;
import it.chalmers.gamma.util.domain.Cid;

import it.chalmers.gamma.util.TokenUtils;

import org.springframework.stereotype.Service;

@Service
public class ActivationCodeService implements DeleteEntity<Cid>  {

    private final ActivationCodeFinder activationCodeFinder;
    private final ActivationCodeRepository activationCodeRepository;

    public ActivationCodeService(ActivationCodeFinder activationCodeFinder,
                                 ActivationCodeRepository activationCodeRepository) {
        this.activationCodeFinder = activationCodeFinder;
        this.activationCodeRepository = activationCodeRepository;
    }

    public ActivationCodeDTO saveActivationCode(Cid cid) {
        // Delete if there was a code previously saved
        // TODO: Fix this so it's the correct exception
        try {
            delete(cid);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        String code = TokenUtils.generateToken(8, TokenUtils.CharacterTypes.NUMBERS);
        return this.activationCodeFinder.toDTO(this.activationCodeRepository.save(new ActivationCode(cid, code)));
    }

    public void delete(Cid cid) throws EntityNotFoundException {
        this.activationCodeRepository.deleteById(cid);
    }

}
