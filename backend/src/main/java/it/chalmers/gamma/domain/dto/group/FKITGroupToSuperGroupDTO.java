package it.chalmers.gamma.domain.dto.group;

public class FKITGroupToSuperGroupDTO {
    private final FKITSuperGroupDTO superGroup;
    private final FKITGroupDTO group;

    public FKITGroupToSuperGroupDTO(FKITSuperGroupDTO superGroup, FKITGroupDTO group) {
        this.superGroup = superGroup;
        this.group = group;
    }

    public FKITSuperGroupDTO getSuperGroup() {
        return this.superGroup;
    }

    public FKITGroupDTO getGroup() {
        return this.group;
    }
}
