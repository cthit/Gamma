package it.chalmers.gamma.domain.dto.website;

import java.util.Objects;
import java.util.UUID;

public class WebsiteDTO {
    private final UUID id;
    private final String name;
    private final String prettyName;

    public WebsiteDTO(UUID id, String name, String prettyName) {
        this.id = id;
        this.name = name;
        this.prettyName = prettyName;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebsiteDTO that = (WebsiteDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.prettyName, that.prettyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.prettyName);
    }

    @Override
    public String toString() {
        return "WebsiteDTO{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + '}';
    }
}
