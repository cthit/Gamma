package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.activationcode.ActivationCodeDTO;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.user.controller.request.CreateITUserRequest;
import it.chalmers.gamma.user.service.UserService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.time.Year;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockITUserFactory {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFinder userFinder;

    public UserDTO generateITUser(String cid, boolean activated) {
        return new UserDTO.UserDTOBuilder().id(UUID.randomUUID()).cid(cid).email(GenerationUtils.generateRandomString()).language(GenerationUtils.generateRandomString()).nick(GenerationUtils.generateRandomString()).firstName(GenerationUtils.generateEmail()).lastName(GenerationUtils.generateRandomString()).phone(Language.SV).avatarUrl("").gdpr(false).userAgreement(true).accountLocked(false).acceptanceYear(Year.of(GenerationUtils.generateIntBetween(2001, 2020))).authorities(activated).build();
    }


    public AdminViewCreateITUserRequest generateValidAdminCreateUserRequest() {
        AdminViewCreateITUserRequest request = new AdminViewCreateITUserRequest();
        request.setAcceptanceYear(GenerationUtils.generateIntBetween(2001, 2020));
        request.setEmail(GenerationUtils.generateEmail());
        request.setFirstName(GenerationUtils.generateRandomString());
        request.setLastName(GenerationUtils.generateRandomString());
        request.setLanguage(Language.SV);
        request.setNick(GenerationUtils.generateRandomString());
        request.setPassword(GenerationUtils.generateRandomString());
        request.setUserAgreement(true);
        request.setCid(GenerationUtils.generateRandomString(10, CharacterTypes.LOWERCASE));
        return request;
    }

    public UserDTO saveUser(UserDTO userDTO) {
        UserDTO user = this.userService.createUser(
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

    public CreateITUserRequest createValidCreateRequest(UserDTO user, ActivationCodeDTO activationCode) {
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
