package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.ActivationCode;
import it.chalmers.gamma.domain.ActivationCodeToken;
import it.chalmers.gamma.domain.Cid;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivationCodeService {

    private final ActivationCodeRepository repository;

    public ActivationCodeService(ActivationCodeRepository repository) {
        this.repository = repository;
    }

    public ActivationCode saveActivationCode(Cid cid) {
        // Delete if there was a code previously saved
        try {
            delete(cid);
        } catch (ActivationCodeNotFoundException ignored) {}

        return this.repository.save(new ActivationCodeEntity(cid, ActivationCodeToken.generate())).toDTO();
    }

    public void delete(Cid cid) throws ActivationCodeNotFoundException {
        try{
            this.repository.deleteById(cid);
        } catch(IllegalArgumentException e) {
            throw new ActivationCodeNotFoundException();
        }
    }

    public List<ActivationCode> getAll() {
        return this.repository.findAll()
                .stream()
                .map(ActivationCodeEntity::toDTO)
                .collect(Collectors.toList());
    }

    public boolean codeMatchesCid(Cid cid, ActivationCodeToken token) {
        //TODO: check if activationcode is valid
        //    @Value("${password-expiration-time}")
        return this.repository.findActivationCodeByCidAndCode(cid, token).isPresent();
    }

    public static class ActivationCodeNotFoundException extends Exception { }


}
