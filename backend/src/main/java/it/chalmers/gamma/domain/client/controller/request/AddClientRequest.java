package it.chalmers.gamma.domain.client.controller.request;

import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public class AddClientRequest {

    @NotNull
    private String webServerRedirectUri;

    @NotNull
    private String name;

    @NotNull
    private boolean autoApprove;

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

    public boolean isAutoApprove() {
        return this.autoApprove;
    }

    public void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddClientRequest that = (AddClientRequest) o;
        return this.autoApprove == that.autoApprove
            && Objects.equals(this.webServerRedirectUri, that.webServerRedirectUri)
            && Objects.equals(this.name, that.name)
            && Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.webServerRedirectUri, this.name, this.autoApprove, this.description);
    }

    @Override
    public String toString() {
        return "AddITClientRequest{"
            + "webServerRedirectUri='" + this.webServerRedirectUri + '\''
            + ", name='" + this.name + '\''
            + ", autoApprove=" + this.autoApprove
            + ", description=" + this.description
            + '}';
    }
}
