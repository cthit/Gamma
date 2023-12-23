package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeRepositoryAdapter;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static it.chalmers.gamma.utils.DomainUtils.committee;
import static it.chalmers.gamma.utils.DomainUtils.digit;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Import({SuperGroupTypeRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class})
public class SuperGroupTypeEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private SuperGroupTypeRepositoryAdapter superGroupTypeRepositoryAdapter;
    @Autowired
    private SuperGroupRepository superGroupRepository;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void Given_ValidType_Expect_save_To_Work() {
        SuperGroupType superGroupType = new SuperGroupType("committee");

        assertThatNoException()
                .isThrownBy(() -> superGroupTypeRepositoryAdapter.add(superGroupType));
    }

    @Test
    public void Given_ValidType_Expect_delete_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        SuperGroupType superGroupType = new SuperGroupType("committee");

        superGroupTypeRepositoryAdapter.add(superGroupType);

        assertThatNoException()
                .isThrownBy(() -> superGroupTypeRepositoryAdapter.delete(superGroupType));

        assertThat(superGroupTypeRepositoryAdapter.getAll())
                .isEmpty();
    }

    @Test
    public void Given_InvalidType_Expect_delete_To_Throw() {
        assertThatExceptionOfType(SuperGroupTypeRepository.SuperGroupTypeNotFoundException.class)
                .isThrownBy(() -> superGroupTypeRepositoryAdapter.delete(new SuperGroupType("committee")));
    }

    @Test
    public void Given_MultipleTypes_Expect_getAll_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepositoryAdapter.add(new SuperGroupType("committee"));
        superGroupTypeRepositoryAdapter.add(new SuperGroupType("alumni"));
        superGroupTypeRepositoryAdapter.add(new SuperGroupType("board"));
        superGroupTypeRepositoryAdapter.add(new SuperGroupType("emailchain"));

        assertThat(superGroupTypeRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(
                        new SuperGroupType("committee"),
                        new SuperGroupType("alumni"),
                        new SuperGroupType("board"),
                        new SuperGroupType("emailchain")
                );
    }

    @Test
    public void Given_SameType_Expect_add_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepositoryAdapter.add(new SuperGroupType("committee"));

        assertThatExceptionOfType(SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException.class)
                .isThrownBy(() -> superGroupTypeRepositoryAdapter.add(new SuperGroupType("committee")));
    }

    @Test
    public void Given_TypeThatIsUsed_Expect_delete_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, SuperGroupTypeRepository.SuperGroupTypeNotFoundException, SuperGroupTypeRepository.SuperGroupTypeHasUsagesException {
        superGroupTypeRepositoryAdapter.add(committee);
        superGroupRepository.save(digit);

        assertThatExceptionOfType(SuperGroupTypeRepository.SuperGroupTypeHasUsagesException.class)
                .isThrownBy(() -> superGroupTypeRepositoryAdapter.delete(committee));
    }

}
