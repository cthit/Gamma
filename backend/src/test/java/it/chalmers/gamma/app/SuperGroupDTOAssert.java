package it.chalmers.gamma.app;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;

public class SuperGroupDTOAssert extends AbstractAssert<SuperGroupDTOAssert, SuperGroupFacade.SuperGroupDTO> {

    protected SuperGroupDTOAssert(SuperGroupFacade.SuperGroupDTO superGroupDTO) {
        super(superGroupDTO, SuperGroupDTOAssert.class);
    }

    public static SuperGroupDTOAssert assertThat(SuperGroupFacade.SuperGroupDTO superGroupDTO) {
        return new SuperGroupDTOAssert(superGroupDTO);
    }

    public SuperGroupDTOAssert isEqualTo(SuperGroup superGroup) {
        isNotNull();

        Assertions.assertThat(actual)
                .hasOnlyFields("id", "version", "name", "prettyName", "type", "svDescription", "enDescription")
                .isEqualTo(new SuperGroupFacade.SuperGroupDTO(
                        superGroup.id().value(),
                        superGroup.version(),
                        superGroup.name().value(),
                        superGroup.prettyName().value(),
                        superGroup.type().value(),
                        superGroup.description().sv().value(),
                        superGroup.description().en().value()
                ));

        return this;
    }

}
