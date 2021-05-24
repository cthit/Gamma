package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.Code;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.Cid;

import org.springframework.stereotype.Service;

@Service
public class ActivationCodeService implements DeleteEntity<Cid>  {

    private final ActivationCodeRepository activationCodeRepository;

    public ActivationCodeService(ActivationCodeRepository activationCodeRepository) {
        this.activationCodeRepository = activationCodeRepository;
    }

    public ActivationCodeDTO saveActivationCode(Cid cid) {
        // Delete if there was a code previously saved
        try {
            delete(cid);
        } catch (EntityNotFoundException ignored) {}

        return this.activationCodeRepository.save(new ActivationCodeEntity(cid, Code.generate())).toDTO();
    }

    public void delete(Cid cid) throws EntityNotFoundException {
        try{
            this.activationCodeRepository.deleteById(cid);
        } catch(IllegalArgumentException e) {
            throw new EntityNotFoundException();
        }
    }

}
