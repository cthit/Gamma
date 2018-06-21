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

    private String[] words = {"ITSMURFEN", "DIGIT<3DIDIT", "SOCKERARGOTT", "HUBBEN2.0"};        // Add some random words in here.

    public ActivationCodeService(ActivationCodeRepository activationCodeRepository){
        this.activationCodeRepository = activationCodeRepository;
    }

    public String generateActivationCode(){
        Random rand = new Random();
        StringBuilder word = new StringBuilder(words[rand.nextInt(words.length-1)]);
        char[] code = new char[word.length()];
        for(int i = 0; i < code.length; i++){
            int index = rand.nextInt(word.length());
            code[i] = word.charAt(index);
            word.replace(index, index+1, "");
        }
        return String.valueOf(code);
    }

    public ActivationCode saveActivationCode(Whitelist cid, String code){
        ActivationCode activationCode = new ActivationCode(cid);
        activationCode.setId(UUID.randomUUID());
        activationCode.setCode(code);
        activationCodeRepository.save(activationCode);
        return activationCode;
    }

    public boolean codeMatches(String code, Whitelist user){
        System.out.println("user is: " + activationCodeRepository.findByCid(user));

        return true;
    }

    // TODO Delete entry after 1 hour or once code has been used. This does not work.
    public void deleteCode(Whitelist cid){
        activationCodeRepository.delete(activationCodeRepository.findByCid(cid));
    }


}
