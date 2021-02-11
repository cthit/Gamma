package it.chalmers.gamma.domain.apikey.controller.request;

import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateApiKeyRequest {
    @NotNull
    @Size(min = 1, max = 30, message = "NAME_MUST_BE_BETWEEN_1_AND_30_CHARACTERS")
    private String name;
    private Text description;

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
    public String toString() {
        return "CreateApiKeyRequest{"
                + "name='" + this.name + '\''
                + ", description='" + this.description.toString() + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateApiKeyRequest that = (CreateApiKeyRequest) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.description);
    }
}
