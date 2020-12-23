package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.user.request.requests.WhitelistCodeRequest;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.utils.CharacterTypes;
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
                GenerationUtils.generateRandomString(15, CharacterTypes.NUMBERS),
                Instant.now(),
                100_000 // Easier to read
        );
    }

    public ActivationCodeDTO saveActivationCode(WhitelistDTO whitelist) {
        return this.activationCodeService.saveActivationCode(whitelist);
    }

    public WhitelistCodeRequest createValidRequest(WhitelistDTO whitelist) {
        WhitelistCodeRequest request =  new WhitelistCodeRequest();
        request.setCid(whitelist.getCid());
        return request;
    }
}
