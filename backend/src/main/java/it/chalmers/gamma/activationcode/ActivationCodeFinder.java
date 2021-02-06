package it.chalmers.gamma.activationcode;

import it.chalmers.gamma.activationcode.exception.ActivationCodeNotFoundException;
import it.chalmers.gamma.domain.Cid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivationCodeFinder {

    private final ActivationCodeRepository repository;

    public ActivationCodeFinder(ActivationCodeRepository repository) {
        this.repository = repository;
    }

    public boolean cidHasCode(Cid cid) {
        return this.repository.existsById(cid.value);
    }

    public ActivationCodeDTO getActivationCodeByCid(Cid cid) throws ActivationCodeNotFoundException {
        return toDTO(
                this.repository.findById(cid.value)
                        .orElseThrow(ActivationCodeNotFoundException::new)
        );
    }

    public ActivationCodeDTO getActivationCodeByCidAndCode(Cid cid, String code) throws ActivationCodeNotFoundException {
        return toDTO(
                this.repository.findActivationCodeByCidAndCode(cid.value, code)
                        .orElseThrow(ActivationCodeNotFoundException::new)
        );
    }

    public List<ActivationCodeDTO> getActivationCodes() {
        return this.repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    protected ActivationCodeDTO toDTO(ActivationCode activationCode) {
        return new ActivationCodeDTO(
                new Cid(activationCode.getCid()),
                activationCode.getCode(),
                activationCode.getCreatedAt()
        );
    }
}