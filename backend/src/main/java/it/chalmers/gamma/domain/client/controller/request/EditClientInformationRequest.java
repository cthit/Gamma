package it.chalmers.gamma.domain.client.controller.request;

import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;

public class EditClientInformationRequest {

    private Text description;

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditClientInformationRequest that = (EditClientInformationRequest) o;
        return Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return "EditClientInformationRequest{" +
                "description=" + description +
                '}';
    }
}
