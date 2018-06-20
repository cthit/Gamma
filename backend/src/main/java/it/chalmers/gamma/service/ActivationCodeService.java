package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.db.repository.ActivationCodeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;

    private String[] words = {"ITSMURFEN", "DIGIT<3DIDIT", "SOCKERARGOTT", "HUBBEN2.0"};        // Add some random words in here.

    private String senderEmail = "";    // This should be taken from a non-public source, like env var.

    public ActivationCodeService(ActivationCodeRepository activationCodeRepository){
        this.activationCodeRepository = activationCodeRepository;
    }

    private String generateActivationCode(){
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

    public void generateAndSendActivationCode(Whitelist whitelist){
        String code = generateActivationCode();
        ActivationCode activationCode = new ActivationCode(whitelist.getId());
        activationCode.setCode(code);
        activationCodeRepository.save(activationCode);
    }
}
