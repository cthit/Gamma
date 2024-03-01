package it.chalmers.gamma.domain.dto.website;

import java.util.Objects;
import java.util.UUID;

public class WebsiteUrlDTO {
    private final UUID id;
    private final String url;
    private final WebsiteDTO websiteDTO;

    public WebsiteUrlDTO(UUID id, String url, WebsiteDTO websiteDTO) {
        this.id = id;
        this.url = url;
        this.websiteDTO = websiteDTO;
    }

    public WebsiteUrlDTO(String url, WebsiteDTO websiteDTO) {
        this(null, url, websiteDTO);
    }

    public UUID getId() {
        return this.id;
    }

    public String getUrl() {
        return this.url;
    }

    public WebsiteDTO getWebsiteDTO() {
        return this.websiteDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebsiteUrlDTO that = (WebsiteUrlDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.url, that.url)
                && Objects.equals(this.websiteDTO, that.websiteDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.url, this.websiteDTO);
    }

    @Override
    public String toString() {
        return "WebsiteUrlDTO{"
                + "id=" + this.id
                + ", url='" + this.url + '\''
                + ", websiteDTO=" + this.websiteDTO
                + '}';
    }
}
