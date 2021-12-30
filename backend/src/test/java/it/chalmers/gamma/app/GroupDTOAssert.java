package it.chalmers.gamma.app;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.Group;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.UUID;

public class GroupDTOAssert extends AbstractAssert<GroupDTOAssert, GroupFacade.GroupDTO> {

    protected GroupDTOAssert(GroupFacade.GroupDTO groupDTO) {
        super(groupDTO, GroupDTOAssert.class);
    }

    public static GroupDTOAssert assertThat(GroupFacade.GroupDTO groupDTO) {
        return new GroupDTOAssert(groupDTO);
    }

    public GroupDTOAssert isEqualTo(Group group) {
        isNotNull();

        Assertions.assertThat(actual)
                .hasOnlyFields("id", "name", "prettyName", "superGroup");

        hasId(group.id().value());
        hasName(group.name().value());
        hasPrettyName(group.prettyName().value());

        SuperGroupDTOAssert.assertThat(actual.superGroup())
                .isEqualTo(group.superGroup());

        return this;
    }

    private GroupDTOAssert hasId(UUID id) {
        Assertions.assertThat(actual.id())
                .isEqualTo(id);
        return this;
    }

    private GroupDTOAssert hasName(String name) {
        Assertions.assertThat(actual.name())
                .isEqualTo(name);
        return this;
    }

    private GroupDTOAssert hasPrettyName(String prettyName) {
        Assertions.assertThat(actual.prettyName())
                .isEqualTo(prettyName);
        return this;
    }

}
