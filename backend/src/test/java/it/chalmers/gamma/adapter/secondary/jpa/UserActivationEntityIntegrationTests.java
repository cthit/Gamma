package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserActivationRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.whitelist.WhitelistRepositoryAdapter;
import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.whitelist.WhitelistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserActivationRepositoryAdapter.class,
        WhitelistRepositoryAdapter.class})
public class UserActivationEntityIntegrationTests {

    @Autowired
    private UserActivationRepositoryAdapter userActivationRepositoryAdapter;

    @Autowired
    private WhitelistRepository whitelistRepository;

    @Test
    void Given_ValidCid_Expect_createActivationToken_To_CreateAValidUserActivationToken() throws WhitelistRepository.AlreadyWhitelistedException {
        Cid asdf = new Cid("asdf");

        whitelistRepository.whitelist(asdf);

        assertThatNoException()
                .isThrownBy(() -> {
                    UserActivationToken userActivationToken = userActivationRepositoryAdapter.createActivationToken(asdf);
                    assertThat(userActivationToken)
                            .isNotNull()
                            .extracting(UserActivationToken::value)
                            .isNotNull()
                            .isNotEqualTo("");
                });
    }

    @Test
    public void Given_ValidCid_Expect_removeActivation_To_Work() throws WhitelistRepository.AlreadyWhitelistedException, UserActivationRepository.CidNotWhitelistedException {
        Cid asdf = new Cid("asdf");

        whitelistRepository.whitelist(asdf);

        userActivationRepositoryAdapter.createActivationToken(asdf);

        assertThatNoException()
                .isThrownBy(() -> userActivationRepositoryAdapter.removeActivation(asdf));

        assertThat(userActivationRepositoryAdapter.get(asdf))
                .isEmpty();
    }

    @Test
    public void Given_MultipleActivations_Expect_getAll_To_ReturnThem() throws WhitelistRepository.AlreadyWhitelistedException, UserActivationRepository.CidNotWhitelistedException {
        Cid cid1 = new Cid("asdf"),
                cid2 = new Cid("fdas"),
                cid3 = new Cid("lmao");

        whitelistRepository.whitelist(cid1);
        whitelistRepository.whitelist(cid2);
        whitelistRepository.whitelist(cid3);

        userActivationRepositoryAdapter.createActivationToken(cid1);
        userActivationRepositoryAdapter.createActivationToken(cid2);
        userActivationRepositoryAdapter.createActivationToken(cid3);

        List<UserActivation> activations = userActivationRepositoryAdapter.getAll();

        //Make sure that the activations have the cids
        assertThat(activations)
                .hasSize(3)
                .extracting(UserActivation::cid)
                .containsExactlyInAnyOrder(cid1, cid2, cid3);

        //Make sure that the activations are unique
        assertThat(activations)
                .extracting(UserActivation::token)
                .doesNotHaveDuplicates();
    }

    @Test
    public void Given_ValidCid_Expect_get_To_ReturnCorrespondingActivation() throws WhitelistRepository.AlreadyWhitelistedException, UserActivationRepository.CidNotWhitelistedException {
        Cid cid1 = new Cid("asdf");

        whitelistRepository.whitelist(cid1);

        userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(userActivationRepositoryAdapter.get(cid1))
                .isNotEmpty()
                .get()
                .extracting(UserActivation::cid)
                .isEqualTo(cid1);
    }

    @Test
    public void Given_ValidCid_Expect_getByToken_To_ReturnCorrespondingActivation() throws WhitelistRepository.AlreadyWhitelistedException, UserActivationRepository.CidNotWhitelistedException {
        Cid cid1 = new Cid("asdf");

        whitelistRepository.whitelist(cid1);

        UserActivationToken userActivationToken = userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(userActivationRepositoryAdapter.getByToken(userActivationToken))
                .isEqualTo(cid1);
    }

    @Test
    public void Given_CidThatIsNotWhitelisted_Expect_createActivationToken_To_Throw() {
        Cid cid1 = new Cid("asdf");

        assertThatExceptionOfType(UserActivationRepository.CidNotWhitelistedException.class)
                .isThrownBy(() -> userActivationRepositoryAdapter.createActivationToken(cid1));
    }

    @Test
    public void Given_InvalidCid_Expect_get_To_ReturnEmpty() throws WhitelistRepository.AlreadyWhitelistedException, UserActivationRepository.CidNotWhitelistedException {
        Cid cid1 = new Cid("asdf"),
                cid2 = new Cid("fdsa");

        whitelistRepository.whitelist(cid1);
        whitelistRepository.whitelist(cid2);

        userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(userActivationRepositoryAdapter.get(cid2))
                .isEmpty();
    }

    @Test
    public void Given_NoActivatedUser_Expect_removeActivation_To_Throw() {
        Cid cid1 = new Cid("asdf");

    }

    @Test
    public void Given_AnAlreadyActivatedCid_Expect_createActivationToken_To_GenerateANewToken() throws WhitelistRepository.AlreadyWhitelistedException, UserActivationRepository.CidNotWhitelistedException {
        Cid cid1 = new Cid("asdf");

        whitelistRepository.whitelist(cid1);

        UserActivationToken token1 = userActivationRepositoryAdapter.createActivationToken(cid1);
        UserActivationToken token2 = userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(token2)
                .isNotNull();

        assertThat(token1)
                .isNotNull()
                .isNotEqualTo(token2);
    }

    @Test
    public void Given_InvalidCid_Expect_getByToken_To_Throw() throws WhitelistRepository.AlreadyWhitelistedException, UserActivationRepository.CidNotWhitelistedException {
        Cid cid1 = new Cid("asdf");
        whitelistRepository.whitelist(cid1);
        this.userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThatExceptionOfType(UserActivationRepository.TokenNotActivatedException.class)
                .isThrownBy(() -> userActivationRepositoryAdapter.getByToken(UserActivationToken.generate()));
    }

}