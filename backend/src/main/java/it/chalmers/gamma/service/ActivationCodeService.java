package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"TooManyMethods"})

public class ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;

    public ActivationCodeService(ActivationCodeRepository activationCodeRepository) {
        this.activationCodeRepository = activationCodeRepository;
    }

    /**
     * connects and places a whitelisted user and a code in the database.
     *
     * @param cid  the already whiteslisted user
     * @param code the code that is associated with a user
     * @return a copy of the ActivationCode object added to the database
     */
    public ActivationCode saveActivationCode(Whitelist cid, String code) {
        if (userHasCode(cid.getCid())) {
            this.activationCodeRepository.delete(
                    this.activationCodeRepository.findByCid_Cid(cid.getCid())
            );
        }
        ActivationCode activationCode = new ActivationCode(cid);
        activationCode.setCode(code);
        this.activationCodeRepository.save(activationCode);
        return activationCode;
    }

    public boolean codeMatches(String code, String user) {
        ActivationCode activationCode = this.activationCodeRepository.findByCid_Cid(user);
        if (activationCode == null) {
            return false;
        }
        return activationCode.getCode().equals(code);
    }

    public boolean userHasCode(String cid) {
        return this.activationCodeRepository.findByCid_Cid(cid) != null;
    }

    // TODO Delete entry after 1 hour or once code has been used. This does not work.
    public void deleteCode(String cid) {
        this.activationCodeRepository.delete(this.activationCodeRepository.findByCid_Cid(cid));
    }

    public void deleteCode(UUID id) {
        this.activationCodeRepository.deleteById(id);
    }

    public boolean codeExists(UUID id) {
        return this.activationCodeRepository.existsById(id);
    }

    /**
     * checks if a user has an expired code connected to their account.
     *
     * @param user  the name of the user to check
     * @param hours the expiration time currently set by the system
     * @return true of the code has expired, false if not
     */
    public boolean hasCodeExpired(String user, double hours) {
        ActivationCode activationCode = this.activationCodeRepository.findByCid_Cid(user);
        return activationCode.getCreatedAt().getEpochSecond()
            + (hours * 3600) < Instant.now().getEpochSecond();
    }

    public List<ActivationCode> getAllActivationCodes() {
        return this.activationCodeRepository.findAll();
    }

    public ActivationCode getActivationCode(UUID id) {
        return this.activationCodeRepository.findById(id).orElse(null);
    }
}
