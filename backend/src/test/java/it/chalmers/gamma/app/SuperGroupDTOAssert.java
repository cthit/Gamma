package it.chalmers.gamma.app;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import org.assertj.core.api.AbstractAssert;

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

        throw new UnsupportedOperationException();
    }

}
