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
        return this.urlRedirect;
    }

    public void setUrlRedirect(String urlRedirect) {
        this.urlRedirect = urlRedirect;
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
        return Objects.equals(this.urlRedirect, that.urlRedirect)
            && Objects.equals(this.name, that.name)
            && Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.urlRedirect, this.name, this.description);
    }

    @Override
    public String toString() {
        return "AddITClientRequest{"
            + ", urlRedirect='" + this.urlRedirect + '\''
            + ", name='" + this.name + '\''
            + ", description=" + this.description
            + '}';
    }
}
