package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.user.ITUserDTO;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.user.ITUserFinder;
import it.chalmers.gamma.user.request.CreateITUserRequest;
import it.chalmers.gamma.user.ITUserService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.time.Year;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockITUserFactory {

    @Autowired
    private ITUserService userService;

    @Autowired
    private ITUserFinder userFinder;

    public ITUserDTO generateITUser(String cid, boolean activated) {
        return new ITUserDTO(
                UUID.randomUUID(),
                cid,
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateEmail(),
                GenerationUtils.generateRandomString(),
                Language.sv,
                "",
                false,
                true,
                false,
                Year.of(GenerationUtils.generateIntBetween(2001, 2020)),
                activated);
    }


    public AdminViewCreateITUserRequest generateValidAdminCreateUserRequest() {
        AdminViewCreateITUserRequest request = new AdminViewCreateITUserRequest();
        request.setAcceptanceYear(GenerationUtils.generateIntBetween(2001, 2020));
        request.setEmail(GenerationUtils.generateEmail());
        request.setFirstName(GenerationUtils.generateRandomString());
        request.setLastName(GenerationUtils.generateRandomString());
        request.setLanguage(Language.sv);
        request.setNick(GenerationUtils.generateRandomString());
        request.setPassword(GenerationUtils.generateRandomString());
        request.setUserAgreement(true);
        request.setCid(GenerationUtils.generateRandomString(10, CharacterTypes.LOWERCASE));
        return request;
    }

    public ITUserDTO saveUser(ITUserDTO userDTO) {
        ITUserDTO user = this.userService.createUser(
                userDTO.getId(),
                userDTO.getNick(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getCid(),
                userDTO.getAcceptanceYear(),
                userDTO.isUserAgreement(),
                userDTO.getEmail(),
                "password"
        );
        this.userService.setAccountActivated(user, userDTO.isActivated());
        return this.userFinder.getUser(new Cid(user.getCid()));
    }

    public CreateITUserRequest createValidCreateRequest(ITUserDTO user, ActivationCodeDTO activationCode) {
        CreateITUserRequest request = new CreateITUserRequest();
        request.setAcceptanceYear(user.getAcceptanceYear().getValue());
        request.setCode(activationCode.getCode());
        request.setEmail(user.getEmail());
        request.setFirstName(user.getFirstName());
        request.setLastName(user.getLastName());
        request.setLanguage(user.getLanguage());
        request.setNick(user.getNick());
        request.setPassword("password");
        request.setUserAgreement(user.isUserAgreement());
        request.setWhitelist(activationCode.getWhitelistDTO());
        return request;
    }
}
