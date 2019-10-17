package it.chalmers.delta.requests;

import it.chalmers.delta.db.entity.Text;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public class AddITClientRequest {

    @NotNull
    private String webServerRedirectUri;

    @NotNull
    private String name;

    private Text description;

    public String getWebServerRedirectUri() {
        return this.webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return this.description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddITClientRequest that = (AddITClientRequest) o;
        return Objects.equals(this.webServerRedirectUri, that.webServerRedirectUri)
            && Objects.equals(this.name, that.name)
            && Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.webServerRedirectUri, this.name, this.description);
    }

    @Override
    public String toString() {
        return "AddITClientRequest{"
            + ", webServerRedirectUri='" + this.webServerRedirectUri + '\''
            + ", name='" + this.name + '\''
            + ", description=" + this.description
            + '}';
    }
}
