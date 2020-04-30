package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import it.chalmers.gamma.service.WebsiteService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockWebsiteFactory {

    @Autowired
    private WebsiteService websiteService;

    public WebsiteDTO generateWebsite() {
        return new WebsiteDTO(
                UUID.randomUUID(),
                GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE),
                GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE)
        );
    }

    public WebsiteDTO saveWebsite(WebsiteDTO website) {
        return this.websiteService.addPossibleWebsite(website.getName(), website.getPrettyName());
    }
}
