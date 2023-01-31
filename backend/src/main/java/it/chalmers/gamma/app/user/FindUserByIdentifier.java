package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
/**
 * Must only be used for security purposes
 */
public class FindUserByIdentifier {

    private final UserRepository userRepository;

    public FindUserByIdentifier(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @param possibleIdentifier Can be a id, email or cid. Checks in that order.
     * @return A user if it can be found with the first valid object.
     */
    public Optional<GammaUser> toUser(String possibleIdentifier) {
        UserIdentifier ui;
        if (isValidUserId(possibleIdentifier)) {
            ui = UserId.valueOf(possibleIdentifier);
        } else if (isValidEmail(possibleIdentifier)) {
            ui = new Email(possibleIdentifier);
        } else if (isValidCid(possibleIdentifier)) {
            ui = new Cid(possibleIdentifier);
        } else {
            ui = null;
        }

        return toUser(ui);
    }

    public Optional<GammaUser> toUser(UserIdentifier userIdentifier) {
        Optional<GammaUser> user = Optional.empty();
        if (userIdentifier instanceof UserId userId) {
            user = this.userRepository.get(userId);
        } else if (userIdentifier instanceof Email email) {
            user = this.userRepository.get(email);
        } else if (userIdentifier instanceof Cid cid) {
            user = this.userRepository.get(cid);
        }
        return user;
    }

    private boolean isValidUserId(String userId) {
        return UserId.validUserId(userId);
    }

    private boolean isValidEmail(String email) {
        return Email.isValidEmail(email);
    }

    private boolean isValidCid(String cid) {
        return Cid.isValidCid(cid);
    }

}
