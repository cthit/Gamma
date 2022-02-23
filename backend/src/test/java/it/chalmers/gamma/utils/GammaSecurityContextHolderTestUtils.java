package it.chalmers.gamma.utils;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientSecret;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.RedirectUrl;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.ApiKeyAuthentication;
import it.chalmers.gamma.security.user.GrantedAuthorityProxy;
import it.chalmers.gamma.security.user.UserDetailsProxy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GammaSecurityContextHolderTestUtils {

    public static final User DEFAULT_USER = new User(
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
                    true,
                    false,
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
            new RedirectUrl("https://mat.chalmers.it"),
            new PrettyName("Mat"),
            new Text(
                    "Klient för mat",
                    "Client for mat"
            ),
            List.of(new AuthorityLevelName("admin")),
            Collections.emptyList(),
            Collections.emptyList(),
            Optional.of(DEFAULT_CLIENT_API_KEY)
    );

    public static User setAuthenticatedAsNormalUser(UserRepository userRepository) {
        setAuthenticatedUser(userRepository, null, DEFAULT_USER, false);
        return DEFAULT_USER.withExtended(DEFAULT_USER.extended().withVersion(1));
    }

    public static User setAuthenticatedAsAdminUser(UserRepository userRepository, AuthorityLevelRepository authorityLevelRepository) {
        setAuthenticatedUser(userRepository, authorityLevelRepository, DEFAULT_USER, true);
        return DEFAULT_USER.withExtended(DEFAULT_USER.extended().withVersion(1));
    }

    public static void setAuthenticatedAsAdminUser(User user) {
        setAuthenticatedUser(null, null, user, true);
    }

    public static void setAuthenticatedUser(UserRepository userRepository,
                                            AuthorityLevelRepository authorityLevelRepository,
                                            User user,
                                            boolean isAdmin) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        String password = "password";

        if (userRepository != null) {
            userRepository.create(user, new UnencryptedPassword(password));
        }

        List<GrantedAuthorityProxy> authorities = new ArrayList<>();
        AuthorityLevelName admin = new AuthorityLevelName("admin");

        if (authorityLevelRepository != null) {
            try {
                authorityLevelRepository.create(admin);

                if (isAdmin) {
                    authorityLevelRepository.save(new AuthorityLevel(
                            admin,
                            new ArrayList<>(),
                            new ArrayList<>(),
                            List.of(user)
                    ));
                }
            } catch (AuthorityLevelRepository.AuthorityLevelAlreadyExistsException e) {
                e.printStackTrace();
            }
        }

        if (isAdmin) {
            authorities.add(new GrantedAuthorityProxy(admin, AuthorityType.AUTHORITY));
        }

        UserDetailsProxy userDetailsProxy = new UserDetailsProxy(
                user,
                "{noop}" + password,
                authorities
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetailsProxy,
                null,
                userDetailsProxy.getAuthorities()
        );
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    public record ClientWithApiKey(Client client, ApiKey apiKey) { }

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

        ApiKeyAuthentication apiKeyAuthentication = new ApiKeyAuthentication(
                DEFAULT_CLIENT.clientApiKey().get().apiKeyToken(),
                AuthorityUtils.NO_AUTHORITIES
        );

        context.setAuthentication(apiKeyAuthentication);
    }

}
