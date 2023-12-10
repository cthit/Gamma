package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserActivationRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.allowlist.AllowListRepositoryAdapter;
import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Import({UserActivationRepositoryAdapter.class,
        AllowListRepositoryAdapter.class})
public class UserActivationEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private UserActivationRepositoryAdapter userActivationRepositoryAdapter;

    @Autowired
    private AllowListRepository allowListRepository;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void Given_ValidCid_Expect_createActivationToken_To_CreateAValidUserActivationToken() throws AllowListRepository.AlreadyAllowedException {
        Cid asdf = new Cid("asdf");

        allowListRepository.allow(asdf);

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
    public void Given_ValidCid_Expect_removeActivation_To_Work() throws AllowListRepository.AlreadyAllowedException, UserActivationRepository.CidNotAllowedException {
        Cid asdf = new Cid("asdf");

        allowListRepository.allow(asdf);

        userActivationRepositoryAdapter.createActivationToken(asdf);

        assertThatNoException()
                .isThrownBy(() -> userActivationRepositoryAdapter.removeActivation(asdf));

        assertThat(userActivationRepositoryAdapter.get(asdf))
                .isEmpty();
    }

    @Test
    public void Given_MultipleActivations_Expect_getAll_To_ReturnThem() throws AllowListRepository.AlreadyAllowedException, UserActivationRepository.CidNotAllowedException {
        Cid cid1 = new Cid("asdf"),
                cid2 = new Cid("fdas"),
                cid3 = new Cid("lmao");

        allowListRepository.allow(cid1);
        allowListRepository.allow(cid2);
        allowListRepository.allow(cid3);

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
    public void Given_ValidCid_Expect_get_To_ReturnCorrespondingActivation() throws AllowListRepository.AlreadyAllowedException, UserActivationRepository.CidNotAllowedException {
        Cid cid1 = new Cid("asdf");

        allowListRepository.allow(cid1);

        userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(userActivationRepositoryAdapter.get(cid1))
                .isNotEmpty()
                .get()
                .extracting(UserActivation::cid)
                .isEqualTo(cid1);
    }

    @Test
    public void Given_ValidCid_Expect_getByToken_To_ReturnCorrespondingActivation() throws AllowListRepository.AlreadyAllowedException, UserActivationRepository.CidNotAllowedException {
        Cid cid1 = new Cid("asdf");

        allowListRepository.allow(cid1);

        UserActivationToken userActivationToken = userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(userActivationRepositoryAdapter.getByToken(userActivationToken))
                .isEqualTo(cid1);
    }

    @Test
    public void Given_CidThatIsNotAllowed_Expect_createActivationToken_To_Throw() {
        Cid cid1 = new Cid("asdf");

        assertThatExceptionOfType(UserActivationRepository.CidNotAllowedException.class)
                .isThrownBy(() -> userActivationRepositoryAdapter.createActivationToken(cid1));
    }

    @Test
    public void Given_InvalidCid_Expect_get_To_ReturnEmpty() throws AllowListRepository.AlreadyAllowedException, UserActivationRepository.CidNotAllowedException {
        Cid cid1 = new Cid("asdf"),
                cid2 = new Cid("fdsa");

        allowListRepository.allow(cid1);
        allowListRepository.allow(cid2);

        userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(userActivationRepositoryAdapter.get(cid2))
                .isEmpty();
    }

    @Test
    public void Given_NoActivatedUser_Expect_removeActivation_To_Throw() {
        Cid cid1 = new Cid("asdf");

    }

    @Test
    public void Given_AnAlreadyActivatedCid_Expect_createActivationToken_To_GenerateANewToken() throws AllowListRepository.AlreadyAllowedException, UserActivationRepository.CidNotAllowedException {
        Cid cid1 = new Cid("asdf");

        allowListRepository.allow(cid1);

        UserActivationToken token1 = userActivationRepositoryAdapter.createActivationToken(cid1);
        UserActivationToken token2 = userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThat(token2)
                .isNotNull();

        assertThat(token1)
                .isNotNull()
                .isNotEqualTo(token2);
    }

    @Test
    public void Given_InvalidCid_Expect_getByToken_To_Throw() throws AllowListRepository.AlreadyAllowedException, UserActivationRepository.CidNotAllowedException {
        Cid cid1 = new Cid("asdf");
        allowListRepository.allow(cid1);
        this.userActivationRepositoryAdapter.createActivationToken(cid1);

        assertThatExceptionOfType(UserActivationRepository.TokenNotActivatedException.class)
                .isThrownBy(() -> userActivationRepositoryAdapter.getByToken(UserActivationToken.generate()));
    }

}
