package it.chalmers.gamma.domain.group.data;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class GroupDTO extends GroupBaseDTO {

    private final SuperGroupDTO superGroup;

    protected GroupDTO(GroupId id,
                       Calendar becomesActive,
                       Calendar becomesInactive,
                       Email email,
                       String name,
                       String prettyName,
                       String avatarURL,
                       SuperGroupDTO superGroup) {
        super(id, becomesActive, becomesInactive, email, name, prettyName, avatarURL);
        this.superGroup = superGroup;
    }

    public SuperGroupDTO getSuperGroup() {
        return superGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupDTO groupDTO = (GroupDTO) o;
        return Objects.equals(superGroup, groupDTO.superGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(superGroup);
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "super: " + super.toString() +
                "superGroup=" + superGroup +
                '}';
    }

    public static class GroupDTOBuilder extends GroupBaseDTOBuilder<GroupDTO, GroupDTOBuilder> {

        private SuperGroupDTO superGroup;

        public GroupDTOBuilder superGroup(SuperGroupDTO superGroup) {
            this.superGroup = superGroup;
            return this;
        }

        @Override
        public GroupDTO build() {
            return new GroupDTO(
                    id,
                    becomesActive,
                    becomesInactive,
                    email,
                    name,
                    prettyName,
                    avatarURL,
                    superGroup
            );
        }

        @Override
        public GroupDTOBuilder from(GroupDTO g) {
            super.from(g);
            this.superGroup = g.getSuperGroup();
            return this;
        }

        @Override
        protected GroupDTOBuilder getThis() {
            return this;
        }
    }

}
