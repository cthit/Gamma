package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.utils.GenerationUtils;
import java.time.Year;
import static org.mockito.Mockito.*;
import java.util.UUID;

public class RandomITUserFactory {
    public static ITUserDTO generateITUser(String username) {
        return new ITUserDTO(
                UUID.randomUUID(),
                username,
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                Language.sv,
                "",
                false,
                true,
                false,
                Year.of(GenerationUtils.generateIntBetween(2001, 2020)),
                true);
    }

    /**
     * Generate User with admin privileges named "admin"
     * @return
     */
    public static ITUserDTO generateAdminUser() {       // TODO
        ITUserDTO user = generateITUser("admin");
        return user;
    }

    public static AdminViewCreateITUserRequest generateValidAdminCreateUserRequest() {
        AdminViewCreateITUserRequest request = new AdminViewCreateITUserRequest();
        request.setAcceptanceYear(GenerationUtils.generateIntBetween(2001, 2020));
        request.setEmail(GenerationUtils.generateEmail());
        request.setFirstName(GenerationUtils.generateRandomString());
        request.setLastName(GenerationUtils.generateRandomString());
        request.setLanguage(Language.sv);
        request.setNick(GenerationUtils.generateRandomString());
        request.setPassword(GenerationUtils.generateRandomString());
        request.setUserAgreement(true);
        request.setCid(GenerationUtils.generateRandomString(10, GenerationUtils.CharacterTypes.LOWERCASE));
        return request;
    }
}
