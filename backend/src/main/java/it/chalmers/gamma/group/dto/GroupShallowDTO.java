package it.chalmers.gamma.group.dto;

import it.chalmers.gamma.domain.text.Text;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class GroupShallowDTO extends GroupBaseDTO {

    private final UUID superGroupId;

    protected GroupShallowDTO(UUID id,
                              Calendar becomesActive,
                              Calendar becomesInactive,
                              Text description,
                              String email,
                              Text function,
                              String name,
                              String prettyName,
                              String avatarURL,
                              UUID superGroupId) {
        super(id, becomesActive, becomesInactive, description, email, function, name, prettyName, avatarURL);
        this.superGroupId = superGroupId;
    }

    public UUID getSuperGroupId() {
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

        private UUID superGroupId;

        @Override
        public GroupShallowDTO build() {
            return new GroupShallowDTO(
                    id,
                    becomesActive,
                    becomesInactive,
                    description,
                    email,
                    function,
                    name,
                    prettyName,
                    avatarURL,
                    superGroupId
            );
        }

        public GroupShallowDTOBuilder superGroupId(UUID id) {
            this.superGroupId = id;
            return this;
        }

        @Override
        protected GroupShallowDTOBuilder getThis() {
            return this;
        }
    }

}
