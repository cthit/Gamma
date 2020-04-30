package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.utils.GenerationUtils;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockActivationCodeFactory {

    @Autowired
    private ActivationCodeService activationCodeService;

    public ActivationCodeDTO generateActivationCode(WhitelistDTO whitelist) {
        return new ActivationCodeDTO(
                UUID.randomUUID(),
                whitelist,
                GenerationUtils.generateRandomString(15, GenerationUtils.CharacterTypes.NUMBERS),
                Instant.now(),
                100000
        );
    }

    public ActivationCodeDTO saveActivationCode(WhitelistDTO whitelist, String code) {
        return this.activationCodeService.saveActivationCode(whitelist, code);
    }
}
