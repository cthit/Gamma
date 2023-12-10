package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.allowlist.AllowListRepositoryAdapter;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Import({AllowListRepositoryAdapter.class})
public class AllowListEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private AllowListRepositoryAdapter allowListRepositoryAdapter;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void Given_Cid_Expect_allow_To_Work() {
        Cid cid = new Cid("asdf");

        assertThatNoException()
                .isThrownBy(() -> allowListRepositoryAdapter.allow(cid));
    }

    @Test
    public void Given_TheSameCidTwice_Expect_allow_To_Throw() {
        Cid cid = new Cid("asdf");

        assertThatNoException()
                .isThrownBy(() -> allowListRepositoryAdapter.allow(cid));
        assertThatExceptionOfType(AllowListRepository.AlreadyAllowedException.class)
                .isThrownBy(() -> allowListRepositoryAdapter.allow(cid));
    }

    @Test
    public void Given_AllowedCid_Expect_remove_To_Work()
            throws AllowListRepository.AlreadyAllowedException {
        Cid cid = new Cid("asdf");

        allowListRepositoryAdapter.allow(cid);
        assertThatNoException()
                .isThrownBy(() -> allowListRepositoryAdapter.remove(cid));
    }

    @Test
    public void Given_CidThatIsNotAllowed_Expect_remove_To_Throw() {
        Cid cid = new Cid("fdsa");

        assertThatExceptionOfType(AllowListRepository.NotOnAllowListException.class)
                .isThrownBy(() -> allowListRepositoryAdapter.remove(cid));
    }

    @Test
    public void Given_AllowedCid_Expect_isAllowed_To_Work()
            throws AllowListRepository.AlreadyAllowedException {
        Cid cid = new Cid("asdf");

        allowListRepositoryAdapter.allow(cid);

        assertThat(allowListRepositoryAdapter.isAllowed(cid))
                .isTrue();
    }

    @Test
    public void Given_ManyAllowedCids_Expect_getAllowList_To_Work() throws AllowListRepository.AlreadyAllowedException {
        Cid cid1 = new Cid("asdf");
        Cid cid2 = new Cid("aasdf");
        Cid cid3 = new Cid("absdf");
        Cid cid4 = new Cid("acsdf");
        Cid cid5 = new Cid("adsdf");

        allowListRepositoryAdapter.allow(cid1);
        allowListRepositoryAdapter.allow(cid2);
        allowListRepositoryAdapter.allow(cid3);
        allowListRepositoryAdapter.allow(cid4);
        allowListRepositoryAdapter.allow(cid5);

        assertThat(allowListRepositoryAdapter.getAllowList())
                .containsExactlyInAnyOrder(cid1, cid2, cid3, cid4, cid5);
    }

}
