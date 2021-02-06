package it.chalmers.gamma.client.dto;

import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;

public class ClientUserAccessDTO {

    private final String name;
    private final Text description;

    public ClientUserAccessDTO(ClientDTO client) {
        this.name = client.getName();
        this.description = client.getDescription();
    }

    public String getName() {
        return this.name;
    }

    public Text getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientUserAccessDTO that = (ClientUserAccessDTO) o;
        return Objects.equals(this.name, that.name)
            && Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.description);
    }

    @Override
    public String toString() {
        return "ITClientUserAccessDTO{"
            + "name='" + this.name + '\''
            + ", description=" + this.description
            + '}';
    }
}
