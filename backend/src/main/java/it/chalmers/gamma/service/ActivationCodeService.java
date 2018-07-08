package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Random;
import java.util.UUID;
@Service
public class ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;

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


    public boolean hasCodeExpired(String user, double hours) {
        ActivationCode activationCode = activationCodeRepository.findByCid_Cid(user);
        return (activationCode.getCreatedAt().getEpochSecond() + (hours * 3600) < Instant.now().getEpochSecond());
    }
}
