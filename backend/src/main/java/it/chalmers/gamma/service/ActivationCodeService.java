package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;

    //TODO should probably change to some other words, or a new system of creating codes

    private String[] words = {"ITSMURFARNA", "DIGIT<3DIDIT", "SOCKERARGOTT", "HUBBEN2.0.1"};        // Add some random words in here.

    public ActivationCodeService(ActivationCodeRepository activationCodeRepository){
        this.activationCodeRepository = activationCodeRepository;
    }

    public String generateActivationCode(){
        Random rand = new Random();
        StringBuilder word = new StringBuilder(words[rand.nextInt(words.length-1)]);
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < 10; i++){
            int index = rand.nextInt(word.length());
            code.append(word.charAt(index));
            word.replace(index, index+1, "");
        }
        return code.toString();
    }

    /**
     * connects and places a whitelisted user and a code in the database
     * @param cid the already whiteslisted user
     * @param code the code that is associated with a user
     * @return a copy of the ActivationCode object added to the database
     */
    public ActivationCode saveActivationCode(Whitelist cid, String code){
        if(userHasCode(cid.getCid())){
            activationCodeRepository.delete(activationCodeRepository.findByCid_Cid(cid.getCid()));
        }
        ActivationCode activationCode = new ActivationCode(cid);
        activationCode.setCode(code);
        activationCodeRepository.save(activationCode);
        return activationCode;
    }

    public boolean codeMatches(String code, String user){
        ActivationCode activationCode = activationCodeRepository.findByCid_Cid(user);
        if(activationCode == null){
            return false;
        }
        return activationCode.getCode().equals(code);
    }
    public boolean userHasCode(String cid){
        return activationCodeRepository.findByCid_Cid(cid) != null;
    }

    // TODO Delete entry after 1 hour or once code has been used. This does not work.
    public void deleteCode(String cid){
        activationCodeRepository.delete(activationCodeRepository.findByCid_Cid(cid));
    }
    public void deleteCode(UUID id){
        activationCodeRepository.deleteById(id);
    }

    public boolean codeExists(UUID id){
        return activationCodeRepository.existsById(id);
    }

    /**
     * checks if a user has an expired code connected to their account
     * @param user the name of the user to check
     * @param hours the expiration time currently set by the system
     * @return true of the code has expired, false if not
     */
    public boolean hasCodeExpired(String user, double hours) {
        ActivationCode activationCode = activationCodeRepository.findByCid_Cid(user);
        return (activationCode.getCreatedAt().getEpochSecond() + (hours * 3600) < Instant.now().getEpochSecond());
    }

    public List<ActivationCode> getAllActivationCodes() {
        return activationCodeRepository.findAll();
    }
}
