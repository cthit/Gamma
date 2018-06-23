package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import org.springframework.stereotype.Service;

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
        Random rand = new Random(10);
        StringBuilder word = new StringBuilder(words[rand.nextInt(words.length-1)]);
        char[] code = new char[word.length()];
        for(int i = 0; i < 10; i++){
            int index = rand.nextInt(word.length());
            code[i] = word.charAt(index);
            word.replace(index, index+1, "");
        }
        return String.valueOf(code);
    }

    public ActivationCode saveActivationCode(Whitelist cid, String code){
        if(userHasCode(cid)){
            activationCodeRepository.delete(activationCodeRepository.findByCid(cid));
        }
        ActivationCode activationCode = new ActivationCode(cid);
        activationCode.setId(UUID.randomUUID());
        activationCode.setCode(code);
        activationCodeRepository.save(activationCode);
        return activationCode;
    }

    public boolean codeMatches(String code, Whitelist user){
        return activationCodeRepository.findByCid(user).getCode().equals(code);
    }
    public boolean userHasCode(Whitelist code){
        return activationCodeRepository.findByCid(code) != null;
    }

    // TODO Delete entry after 1 hour or once code has been used. This does not work.
    public void deleteCode(Whitelist cid){
        activationCodeRepository.delete(activationCodeRepository.findByCid(cid));
    }


}
