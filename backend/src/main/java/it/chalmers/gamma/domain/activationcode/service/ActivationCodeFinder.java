package it.chalmers.gamma.domain.activationcode.service;

import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.Cid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivationCodeFinder implements GetAllEntities<ActivationCodeDTO> {

    private final ActivationCodeRepository repository;

    public ActivationCodeFinder(ActivationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ActivationCodeDTO> getAll() {
        return this.repository.findAll()
                .stream()
                .map(ActivationCode::toDTO)
                .collect(Collectors.toList());
    }

    public boolean codeMatchesCid(Cid cid, Code code) {
        //TODO: check if activationcode is valid
        //    @Value("${password-expiration-time}")
        return this.repository.findActivationCodeByCidAndCode(cid, code).isPresent();
    }

}
