package it.chalmers.gamma.domain.activationcode.service;

import it.chalmers.gamma.domain.activationcode.Code;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.domain.activationcode.data.db.ActivationCode;
import it.chalmers.gamma.domain.activationcode.data.dto.ActivationCodeDTO;
import it.chalmers.gamma.domain.activationcode.data.db.ActivationCodeRepository;
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
        return this.repository.findActivationCodeByCidAndCode(cid, code).isPresent();
    }

}
