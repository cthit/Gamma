package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.user.request.requests.AddAuthorityLevelRequest;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockAuthorityLevelFactory {

    @Autowired
    private AuthorityLevelService authorityLevelService;

    public AuthorityLevelDTO generateAuthorityLevel() {
        return new AuthorityLevelDTO(
                UUID.randomUUID(),
                GenerationUtils.generateRandomString(10, CharacterTypes.LOWERCASE)
        );
    }

    public AuthorityLevelDTO saveAuthorityLevel(AuthorityLevelDTO authorityLevel) {
        return this.authorityLevelService.addAuthorityLevel(authorityLevel.getAuthority());
    }

    public AddAuthorityLevelRequest createValidRequest(AuthorityLevelDTO authorityLevel) {
        AddAuthorityLevelRequest request = new AddAuthorityLevelRequest();
        request.setAuthorityLevel(authorityLevel.getAuthority());
        return request;
    }
}
