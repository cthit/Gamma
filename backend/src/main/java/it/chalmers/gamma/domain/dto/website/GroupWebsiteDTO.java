package it.chalmers.gamma.domain.dto.website;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import java.util.Objects;
import java.util.UUID;

public class GroupWebsiteDTO implements WebsiteInterfaceDTO {
    private final UUID id;
    private final WebsiteUrlDTO website;
    private final FKITGroupDTO groupDTO;


    public GroupWebsiteDTO(UUID id, WebsiteUrlDTO website, FKITGroupDTO groupDTO) {
        this.id = id;
        this.website = website;
        this.groupDTO = groupDTO;
    }

    public UUID getId() {
        return this.id;
    }

    @Override
    public WebsiteUrlDTO getWebsiteURL() {
        return this.website;
    }

    public FKITGroupDTO getGroupDTO() {
        return this.groupDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupWebsiteDTO that = (GroupWebsiteDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.website, that.website)
                && Objects.equals(this.groupDTO, that.groupDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.website, this.groupDTO);
    }

    @Override
    public String toString() {
        return "GroupWebsiteDTO{"
                + "id=" + this.id
                + ", website=" + this.website
                + ", groupDTO=" + this.groupDTO
                + '}';
    }
}
