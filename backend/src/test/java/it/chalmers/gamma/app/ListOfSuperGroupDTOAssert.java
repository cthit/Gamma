package it.chalmers.gamma.app;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;

public class ListOfSuperGroupDTOAssert extends AbstractAssert<ListOfSuperGroupDTOAssert, List<SuperGroupFacade.SuperGroupDTO>> {

    protected ListOfSuperGroupDTOAssert(List<SuperGroupFacade.SuperGroupDTO> superGroupDTOS) {
        super(superGroupDTOS, ListOfSuperGroupDTOAssert.class);
    }

    public static ListOfSuperGroupDTOAssert assertThat(List<SuperGroupFacade.SuperGroupDTO> superGroupDTOS) {
        return new ListOfSuperGroupDTOAssert(superGroupDTOS);
    }

    /**
     * Requires the same order
     */
    public ListOfSuperGroupDTOAssert hasSameElements(List<SuperGroup> superGroups) {
        isNotNull();

        Assertions.assertThat(actual.size())
                .isEqualTo(superGroups.size());

        for (int i = 0; i < superGroups.size(); i++) {
            SuperGroupFacade.SuperGroupDTO superGroupDTO = actual.get(i);
            SuperGroup superGroup = superGroups.get(i);

            SuperGroupDTOAssert.assertThat(superGroupDTO)
                    .isEqualTo(superGroup);
        }

        return this;
    }

}
