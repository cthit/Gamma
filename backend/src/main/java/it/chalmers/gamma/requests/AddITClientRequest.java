package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public class AddITClientRequest {

    @NotNull
    private String urlRedirect;

    @NotNull
    private String name;

    private Text description;

    public String getUrlRedirect() {
        return urlRedirect;
    }

    public void setUrlRedirect(String urlRedirect) {
        this.urlRedirect = urlRedirect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return description;
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
        return Objects.equals(urlRedirect, that.urlRedirect) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(urlRedirect, name, description);
    }

    @Override
    public String toString() {
        return "AddITClientRequest{" +
            ", urlRedirect='" + urlRedirect + '\'' +
            ", name='" + name + '\'' +
            ", description=" + description +
            '}';
    }
}
