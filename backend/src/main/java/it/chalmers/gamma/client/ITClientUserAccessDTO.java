package it.chalmers.gamma.client;

import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;

public class ITClientUserAccessDTO {

    private final String name;
    private final Text description;

    public ITClientUserAccessDTO(ITClientDTO itClientDTO) {
        this.name = itClientDTO.getName();
        this.description = itClientDTO.getDescription();
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
        ITClientUserAccessDTO that = (ITClientUserAccessDTO) o;
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
