package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.whitelist.WhitelistRepositoryAdapter;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.whitelist.WhitelistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ActiveProfiles("test")
@Import({WhitelistRepositoryAdapter.class})
public class WhitelistEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private WhitelistRepositoryAdapter whitelistRepositoryAdapter;

    @Test
    public void Given_Cid_Expect_whitelist_To_Work() {
        Cid cid = new Cid("asdf");

        assertThatNoException()
                .isThrownBy(() -> whitelistRepositoryAdapter.whitelist(cid));
    }

    @Test
    public void Given_TheSameCidTwice_Expect_whitelist_To_Throw() {
        Cid cid = new Cid("asdf");

        assertThatNoException()
                .isThrownBy(() -> whitelistRepositoryAdapter.whitelist(cid));
        assertThatExceptionOfType(WhitelistRepository.AlreadyWhitelistedException.class)
                .isThrownBy(() -> whitelistRepositoryAdapter.whitelist(cid));
    }

    @Test
    public void Given_WhitelistedCid_Expect_remove_To_Work()
            throws WhitelistRepository.AlreadyWhitelistedException {
        Cid cid = new Cid("asdf");

        whitelistRepositoryAdapter.whitelist(cid);
        assertThatNoException()
                .isThrownBy(() -> whitelistRepositoryAdapter.remove(cid));
    }

    @Test
    public void Given_CidThatIsNotWhitelisted_Expect_remove_To_Throw() {
        Cid cid = new Cid("fdsa");

        assertThatExceptionOfType(WhitelistRepository.NotWhitelistedException.class)
                .isThrownBy(() -> whitelistRepositoryAdapter.remove(cid));
    }

    @Test
    public void Given_WhitelistedCid_Expect_isWhitelisted_To_Work()
            throws WhitelistRepository.AlreadyWhitelistedException {
        Cid cid = new Cid("asdf");

        whitelistRepositoryAdapter.whitelist(cid);

        assertThat(whitelistRepositoryAdapter.isWhitelisted(cid))
                .isTrue();
    }

    @Test
    public void Given_ManyWhitelistedCids_Expect_getWhitelist_To_Work() throws WhitelistRepository.AlreadyWhitelistedException {
        Cid cid1 = new Cid("asdf");
        Cid cid2 = new Cid("aasdf");
        Cid cid3 = new Cid("absdf");
        Cid cid4 = new Cid("acsdf");
        Cid cid5 = new Cid("adsdf");

        whitelistRepositoryAdapter.whitelist(cid1);
        whitelistRepositoryAdapter.whitelist(cid2);
        whitelistRepositoryAdapter.whitelist(cid3);
        whitelistRepositoryAdapter.whitelist(cid4);
        whitelistRepositoryAdapter.whitelist(cid5);

        assertThat(whitelistRepositoryAdapter.getWhitelist())
                .containsExactlyInAnyOrder(cid1, cid2, cid3, cid4, cid5);
    }

}
