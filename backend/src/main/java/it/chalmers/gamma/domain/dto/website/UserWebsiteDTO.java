package it.chalmers.gamma.domain.dto.website;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.Objects;
import java.util.UUID;

public class UserWebsiteDTO implements WebsiteInterfaceDTO {
    private final UUID id;
    private final ITUserDTO itUserDTO;
    private final WebsiteUrlDTO websiteUrlDTO;

    public UserWebsiteDTO(UUID id, ITUserDTO itUserDTO, WebsiteUrlDTO websiteUrlDTO) {
        this.id = id;
        this.itUserDTO = itUserDTO;
        this.websiteUrlDTO = websiteUrlDTO;
    }

    public UUID getId() {
        return this.id;
    }

    public ITUserDTO getItUserDTO() {
        return this.itUserDTO;
    }
    @Override
    public WebsiteUrlDTO getWebsiteURL() {
        return this.websiteUrlDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserWebsiteDTO that = (UserWebsiteDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.itUserDTO, that.itUserDTO)
                && Objects.equals(this.websiteUrlDTO, that.websiteUrlDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.itUserDTO, this.websiteUrlDTO);
    }

    @Override
    public String toString() {
        return "UserWebsiteDTO{"
                + "id=" + this.id
                + ", itUserDTO=" + this.itUserDTO
                + ", websiteUrlDTO=" + this.websiteUrlDTO
                + '}';
    }
}
