package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class CreateApiKeyRequest {
    @NotNull
    @Size(min = 1, max = 30, message = "NAME_MUST_BE_BETWEEN_1_AND_30_CHARACTERS")
    private String name;
    private Text description;

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
    public String toString() {
        return "CreateApiKeyRequest{" +
                "name='" + name + '\'' +
                ", description='" + description.toString() + '\'' +
                '}';
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
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
