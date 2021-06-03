package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.Code;
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

    public ActivationCodeDTO saveActivationCode(Cid cid) {
        // Delete if there was a code previously saved
        try {
            delete(cid);
        } catch (ActivationCodeNotFoundException ignored) {}

        return this.repository.save(new ActivationCodeEntity(cid, Code.generate())).toDTO();
    }

    public void delete(Cid cid) throws ActivationCodeNotFoundException {
        try{
            this.repository.deleteById(cid);
        } catch(IllegalArgumentException e) {
            throw new ActivationCodeNotFoundException();
        }
    }

    public List<ActivationCodeDTO> getAll() {
        return this.repository.findAll()
                .stream()
                .map(ActivationCodeEntity::toDTO)
                .collect(Collectors.toList());
    }

    public boolean codeMatchesCid(Cid cid, Code code) {
        //TODO: check if activationcode is valid
        //    @Value("${password-expiration-time}")
        return this.repository.findActivationCodeByCidAndCode(cid, code).isPresent();
    }

    public static class ActivationCodeNotFoundException extends Exception { }


}
