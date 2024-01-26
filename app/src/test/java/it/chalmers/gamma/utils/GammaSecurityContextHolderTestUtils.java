package it.chalmers.gamma.utils;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.api.ApiAuthenticationToken;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Optional;

public class GammaSecurityContextHolderTestUtils {

    public static final GammaUser DEFAULT_USER = new GammaUser(
            //Same as in the default for WithUser
            UserId.valueOf("e3404d7e-bd03-43ec-ba74-67054fa70d94"),
            new Cid("asdf"),
            new Nick("RandoM"),
            new FirstName("Smurf"),
            new LastName("Smurfsson"),
            new AcceptanceYear(2018),
            Language.EN,
            new UserExtended(
                    new Email("smurf@chalmers.it"),
                    0,
                    false,
                    ImageUri.defaultUserAvatar()
            )
    );

    private static final ApiKey DEFAULT_CLIENT_API_KEY = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("Mat"),
            new Text(
                    "Api nyckel för mat",
                    "Api key for mat"
            ),
            ApiKeyType.CLIENT,
            ApiKeyToken.generate()
    );

    public static final Client DEFAULT_CLIENT = new Client(
            ClientUid.generate(),
            ClientId.generate(),
            ClientSecret.generate(),
            new ClientRedirectUrl("https://mat.chalmers.it"),
            new PrettyName("Mat"),
            new Text(
                    "Klient för mat",
                    "Client for mat"
            ),
            Collections.emptyList(),
            DEFAULT_CLIENT_API_KEY,
            new ClientOwnerOfficial(),
            null
    );

    public static GammaUser setAuthenticatedAsNormalUser(UserRepository userRepository) {
        setAuthenticatedUser(userRepository, null, DEFAULT_USER, false);
        return DEFAULT_USER.withExtended(DEFAULT_USER.extended().withVersion(1));
    }

    public static GammaUser setAuthenticatedAsAdminUser(UserRepository userRepository, AdminRepository adminRepository) {
        setAuthenticatedUser(userRepository, adminRepository, DEFAULT_USER, true);
        return DEFAULT_USER.withExtended(DEFAULT_USER.extended().withVersion(1));
    }

    public static void setAuthenticatedAsAdminUser(GammaUser user) {
        setAuthenticatedUser(null, null, user, true);
    }

    public static void setAuthenticatedUser(UserRepository userRepository,
                                            AdminRepository adminRepository,
                                            GammaUser gammaUser,
                                            boolean isAdmin) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        String password = "password";

        if (userRepository != null) {
            try {
                userRepository.create(gammaUser, new UnencryptedPassword(password));
            } catch (UserRepository.CidAlreadyInUseException | UserRepository.EmailAlreadyInUseException e) {
                e.printStackTrace();
            }
        }

        adminRepository.setAdmin(gammaUser.id(), isAdmin);

        User user = new User(gammaUser.id().value().toString(), "{noop}" + password, Collections.emptyList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.emptyList()
        );
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);


        auth.setDetails(new UserAuthentication() {
            @Override
            public GammaUser get() {
                return gammaUser;
            }

            @Override
            public boolean isAdmin() {
                return isAdmin;
            }
        });
    }

    /**
     * Will have no approved users
     */
    public static ClientWithApiKey setAuthenticatedAsClientWithApi(ClientRepository clientRepository) {
        clientRepository.save(DEFAULT_CLIENT);

        setAuthenticatedAsClientWithApi(DEFAULT_CLIENT);

        return new ClientWithApiKey(DEFAULT_CLIENT, DEFAULT_CLIENT_API_KEY);
    }

    public static void setAuthenticatedAsClientWithApi(Client client) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        if (client.clientApiKey().isEmpty()) {
            throw new IllegalArgumentException();
        }

        context.setAuthentication(ApiAuthenticationToken.fromAuthenticatedApiKey(new ApiAuthentication() {
            @Override
            public ApiKey get() {
                return client.clientApiKey().get();
            }

            @Override
            public Optional<Client> getClient() {
                return Optional.of(client);
            }
        }));

        SecurityContextHolder.setContext(context);
    }

    public record ClientWithApiKey(Client client, ApiKey apiKey) {
    }

}
