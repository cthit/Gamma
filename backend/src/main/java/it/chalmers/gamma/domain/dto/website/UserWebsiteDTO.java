package it.chalmers.gamma.domain.dto.website;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import java.util.Objects;
import java.util.UUID;

public class UserWebsiteDTO implements WebsiteInterfaceDTO {
    private final UUID id;
    private final ITUserDTO itUserDTO;
    private final WebsiteURLDTO websiteURLDTO;

    public UserWebsiteDTO(UUID id, ITUserDTO itUserDTO, WebsiteURLDTO websiteURLDTO) {
        this.id = id;
        this.itUserDTO = itUserDTO;
        this.websiteURLDTO = websiteURLDTO;
    }

    public UUID getId() {
        return id;
    }

    public ITUserDTO getItUserDTO()
    {
        return itUserDTO;
    }
    @Override
    public WebsiteURLDTO getWebsiteURL() {
        return websiteURLDTO;
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
                && Objects.equals(this.websiteURLDTO, that.websiteURLDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.itUserDTO, this.websiteURLDTO);
    }

    @Override
    public String toString() {
        return "UserWebsiteDTO{"
                + "id=" + this.id
                + ", itUserDTO=" + this.itUserDTO
                + ", websiteURLDTO=" + this.websiteURLDTO
                + '}';
    }
}
