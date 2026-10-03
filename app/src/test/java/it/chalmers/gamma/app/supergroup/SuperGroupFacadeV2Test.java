package it.chalmers.gamma.app.supergroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.chalmers.gamma.app.apikey.SuperGroupTypeRestrictions;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.Name;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuperGroupFacadeV2Test {

  private SuperGroupRepository superGroupRepository;
  private SuperGroupTypeRepository superGroupTypeRepository;
  private SuperGroupTypeRestrictions restrictions;
  private SuperGroupFacade facade;

  private static final SuperGroupType TYPE_A = new SuperGroupType("typea");
  private static final SuperGroupType TYPE_B = new SuperGroupType("typeb");

  @BeforeEach
  void setUp() {
    superGroupRepository = mock(SuperGroupRepository.class);
    superGroupTypeRepository = mock(SuperGroupTypeRepository.class);
    restrictions = mock(SuperGroupTypeRestrictions.class);
    facade =
        new SuperGroupFacade(
            mock(it.chalmers.gamma.app.authentication.AccessGuard.class),
            superGroupRepository,
            superGroupTypeRepository,
            mock(GroupRepository.class),
            restrictions);

    when(superGroupRepository.getAll()).thenReturn(List.of(superGroup(TYPE_A), superGroup(TYPE_B)));
  }

  private static SuperGroup superGroup(SuperGroupType type) {
    return new SuperGroup(
        new SuperGroupId(UUID.randomUUID()),
        1,
        new Name("test-group"),
        new PrettyName("test-group-" + type.value()),
        type,
        new Text("test", "test"));
  }

  @Test
  void withoutRestrictionsAllSuperGroupsAreVisible() {
    when(restrictions.allowedTypes()).thenReturn(Optional.empty());

    assertThat(facade.fetchAllSuperGroups()).hasSize(2);
  }

  @Test
  void onlyAllowedTypesAreVisible() {
    when(restrictions.allowedTypes()).thenReturn(Optional.of(List.of(TYPE_A)));

    assertThat(facade.fetchAllSuperGroups())
        .hasSize(1)
        .first()
        .satisfies(dto -> assertThat(dto.type()).isEqualTo("typea"));
  }

  @Test
  void failClosedWhenNoTypesAreAllowed() {
    when(restrictions.allowedTypes()).thenReturn(Optional.of(List.of()));

    assertThat(facade.fetchAllSuperGroups()).isEmpty();
    assertThat(facade.fetchSuperGroup(UUID.randomUUID())).isEmpty();
  }

  @Test
  void fetchSuperGroupFiltersDisallowedType() {
    when(restrictions.allowedTypes()).thenReturn(Optional.of(List.of(TYPE_A)));
    SuperGroup groupOfTypeB = superGroup(TYPE_B);
    when(superGroupRepository.get(groupOfTypeB.id())).thenReturn(Optional.of(groupOfTypeB));

    assertThat(facade.fetchSuperGroup(groupOfTypeB.id().value())).isEmpty();
  }

  @Test
  void fetchSuperGroupReturnsAllowedType() {
    when(restrictions.allowedTypes()).thenReturn(Optional.of(List.of(TYPE_A)));
    SuperGroup groupOfTypeA = superGroup(TYPE_A);
    when(superGroupRepository.get(groupOfTypeA.id())).thenReturn(Optional.of(groupOfTypeA));

    assertThat(facade.fetchSuperGroup(groupOfTypeA.id().value())).isPresent();
  }

  @Test
  void fetchSuperGroupTreeUsesOnlyAllowedTypes() {
    when(restrictions.allowedTypes()).thenReturn(Optional.of(List.of(TYPE_A)));

    assertThat(facade.fetchSuperGroupTree())
        .hasSize(1)
        .first()
        .satisfies(dto -> assertThat(dto.type()).isEqualTo("typea"));
  }
}
