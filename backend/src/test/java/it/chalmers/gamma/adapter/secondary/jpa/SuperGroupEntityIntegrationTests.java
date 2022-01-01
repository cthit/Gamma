package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.group.GroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.GroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static it.chalmers.gamma.DomainUtils.addGroup;
import static it.chalmers.gamma.DomainUtils.alumni;
import static it.chalmers.gamma.DomainUtils.board;
import static it.chalmers.gamma.DomainUtils.committee;
import static it.chalmers.gamma.DomainUtils.didit;
import static it.chalmers.gamma.DomainUtils.digit;
import static it.chalmers.gamma.DomainUtils.digit19;
import static it.chalmers.gamma.DomainUtils.dragit;
import static it.chalmers.gamma.DomainUtils.drawit;
import static it.chalmers.gamma.DomainUtils.emeritus;
import static it.chalmers.gamma.DomainUtils.society;
import static it.chalmers.gamma.DomainUtils.sprit;
import static it.chalmers.gamma.DomainUtils.styrit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        SuperGroupTypeRepositoryAdapter.class,
        GroupRepositoryAdapter.class,
        GroupEntityConverter.class,
        UserRepositoryAdapter.class,
        UserEntityConverter.class,
        PostRepositoryAdapter.class,
        PostEntityConverter.class})
public class SuperGroupEntityIntegrationTests {

    @Autowired
    private SuperGroupRepositoryAdapter superGroupRepositoryAdapter;
    @Autowired
    private SuperGroupTypeRepository superGroupTypeRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    public void Given_ValidSuperGroup_Expect_save_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepository.add(committee);
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
        superGroupTypeRepository.add(committee);
        superGroupRepositoryAdapter.save(digit);

        assertThatExceptionOfType(SuperGroupRepository.NameAlreadyExistsRuntimeException.class)
                .isThrownBy(() -> superGroupRepositoryAdapter.save(digit.withId(SuperGroupId.generate())));
    }

    @Test
    public void Given_ValidSuperGroup_Expect_delete_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, SuperGroupRepository.SuperGroupNotFoundException, SuperGroupRepository.SuperGroupIsUsedException {
        superGroupTypeRepository.add(committee);
        superGroupRepositoryAdapter.save(digit);

        assertThat(superGroupRepositoryAdapter.get(digit.id()))
                .isPresent();

        superGroupRepositoryAdapter.delete(digit.id());

        assertThat(superGroupRepositoryAdapter.get(digit.id()))
                .isEmpty();
    }

    @Test
    public void Given_InvalidSuperGroup_Expect_delete_To_Throw() {
        assertThatExceptionOfType(SuperGroupRepository.SuperGroupNotFoundException.class)
                .isThrownBy(() -> superGroupRepositoryAdapter.delete(SuperGroupId.generate()));
    }

    @Test
    public void Given_MultipleSuperGroup_Expect_getAll_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepository.add(committee);
        superGroupTypeRepository.add(society);
        superGroupTypeRepository.add(alumni);

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

    @Test
    public void Given_MultipleSuperGroup_Expect_getAllByType_To_Work() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepository.add(committee);
        superGroupTypeRepository.add(society);
        superGroupTypeRepository.add(alumni);
        superGroupTypeRepository.add(board);

        superGroupRepositoryAdapter.save(drawit);
        superGroupRepositoryAdapter.save(dragit);
        superGroupRepositoryAdapter.save(digit);
        superGroupRepositoryAdapter.save(didit);
        superGroupRepositoryAdapter.save(sprit);
        superGroupRepositoryAdapter.save(styrit);
        superGroupRepositoryAdapter.save(emeritus);

        assertThat(superGroupRepositoryAdapter.getAllByType(alumni))
                .containsExactlyInAnyOrder(
                        dragit.withVersion(1),
                        didit.withVersion(1),
                        sprit.withVersion(1),
                        emeritus.withVersion(1)
                );
    }

    @Test
    public void Given_SuperGroupThatIsUsed_Expect_delete_To_Throw() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        addGroup(
                superGroupTypeRepository,
                superGroupRepositoryAdapter,
                userRepository,
                postRepository,
                groupRepository,
                digit19
        );

        assertThatExceptionOfType(SuperGroupRepository.SuperGroupIsUsedException.class)
                .isThrownBy(() -> superGroupRepositoryAdapter.delete(digit19.superGroup().id()));
    }

}
