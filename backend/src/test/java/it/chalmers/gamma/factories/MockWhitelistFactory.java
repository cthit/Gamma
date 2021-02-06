package it.chalmers.gamma.factories;

import it.chalmers.gamma.whitelist.dto.WhitelistDTO;
import it.chalmers.gamma.whitelist.request.WhitelistCodeRequest;
import it.chalmers.gamma.whitelist.service.WhitelistService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockWhitelistFactory {

    @Autowired
    private WhitelistService whitelistService;

    public WhitelistDTO generateWhitelist() {
        return new WhitelistDTO(
                UUID.randomUUID(),
                GenerationUtils.generateRandomString(5, CharacterTypes.LOWERCASE)
        );
    }

    public WhitelistDTO saveWhitelist(WhitelistDTO whitelist) {
        return this.whitelistService.addWhiteListedCid(whitelist.getCid());
    }

    public WhitelistCodeRequest createValidRequest(WhitelistDTO whitelist) {
        WhitelistCodeRequest request = new WhitelistCodeRequest();
        request.setCid(whitelist.getCid());
        return request;
    }
}
