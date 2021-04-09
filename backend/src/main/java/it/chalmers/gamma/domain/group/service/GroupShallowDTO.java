package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

import java.util.Calendar;
import java.util.Objects;

public class GroupShallowDTO extends GroupBaseDTO implements DTO {

    private final SuperGroupId superGroupId;

    protected GroupShallowDTO(GroupId id,
                              Calendar becomesActive,
                              Calendar becomesInactive,
                              Email email,
                              String name,
                              String prettyName,
                              String avatarURL,
                              SuperGroupId superGroupId) {
        super(id, becomesActive, becomesInactive, email, name, prettyName, avatarURL);
        this.superGroupId = superGroupId;
    }

    public SuperGroupId getSuperGroupId() {
        return superGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupShallowDTO that = (GroupShallowDTO) o;
        return Objects.equals(superGroupId, that.superGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(superGroupId);
    }

    @Override
    public String toString() {
        return "GroupShallowDTO{" +
                "super: " + super.toString() +
                "superGroupId=" + superGroupId +
                '}';
    }

    public static class GroupShallowDTOBuilder extends GroupBaseDTOBuilder<GroupShallowDTO, GroupShallowDTOBuilder> {

        private SuperGroupId superGroupId;

        @Override
        public GroupShallowDTO build() {
            return new GroupShallowDTO(
                    id,
                    becomesActive,
                    becomesInactive,
                    email,
                    name,
                    prettyName,
                    avatarURL,
                    superGroupId
            );
        }

        public GroupShallowDTOBuilder superGroupId(SuperGroupId id) {
            this.superGroupId = id;
            return this;
        }

        @Override
        protected GroupShallowDTOBuilder getThis() {
            return this;
        }
    }

}
