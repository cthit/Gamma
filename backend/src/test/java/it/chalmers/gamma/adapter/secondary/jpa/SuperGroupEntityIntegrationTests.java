package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeRepositoryAdapter;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static it.chalmers.gamma.DomainUtils.alumni;
import static it.chalmers.gamma.DomainUtils.committee;
import static it.chalmers.gamma.DomainUtils.didit;
import static it.chalmers.gamma.DomainUtils.digit;
import static it.chalmers.gamma.DomainUtils.drawit;
import static it.chalmers.gamma.DomainUtils.society;
import static it.chalmers.gamma.DomainUtils.sprit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        SuperGroupTypeRepositoryAdapter.class})
public class SuperGroupEntityIntegrationTests {

    @Autowired
    private SuperGroupRepositoryAdapter superGroupRepositoryAdapter;
    @Autowired
    private SuperGroupTypeRepositoryAdapter superGroupTypeRepositoryAdapter;

    @Test
    public void Given_ValidSuperGroup_Expect_save_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepositoryAdapter.add(committee);
        superGroupRepositoryAdapter.save(digit);

        assertThat(superGroupRepositoryAdapter.get(digit.id()))
                .get().isEqualTo(digit.withVersion(1));
    }

    @Test
    public void Given_InvalidType_Expect_save_To_Throw() {
        assertThatExceptionOfType(SuperGroupRepository.TypeNotFoundRuntimeException.class)
                .isThrownBy(() -> superGroupRepositoryAdapter.save(digit));
    }

    @Test
    public void Given_SameSuperGroup_Expect_save_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepositoryAdapter.add(committee);
        superGroupRepositoryAdapter.save(digit);

        assertThatExceptionOfType(SuperGroupRepository.NameAlreadyExistsRuntimeException.class)
                .isThrownBy(() -> superGroupRepositoryAdapter.save(digit.withId(SuperGroupId.generate())));
    }

    @Test
    public void Given_ValidSuperGroup_Expect_delete_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, SuperGroupRepository.SuperGroupNotFoundException {
        superGroupTypeRepositoryAdapter.add(committee);
        superGroupRepositoryAdapter.save(digit);

        assertThat(superGroupRepositoryAdapter.get(digit.id()))
                .isPresent();

        superGroupRepositoryAdapter.delete(digit.id());

        assertThat(superGroupRepositoryAdapter.get(digit.id()))
                .isEmpty();
    }

    @Test
    public void Given_MultipleSuperGroup_Expect_getAll_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepositoryAdapter.add(committee);
        superGroupTypeRepositoryAdapter.add(society);
        superGroupTypeRepositoryAdapter.add(alumni);

        superGroupRepositoryAdapter.save(drawit);
        superGroupRepositoryAdapter.save(digit);
        superGroupRepositoryAdapter.save(didit);
        superGroupRepositoryAdapter.save(sprit);

        assertThat(superGroupRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(
                        drawit.withVersion(1),
                        digit.withVersion(1),
                        didit.withVersion(1),
                        sprit.withVersion(1)
                );
    }

}
