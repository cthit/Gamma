package it.chalmers.gamma.requests;

import java.util.Objects;

public class EditMembershipRequest {
    private String unofficialName;

    public String getUnofficialName() {
        return this.unofficialName;
    }

    public void setUnofficialName(String unofficialName) {
        this.unofficialName = unofficialName;
    }

    @Override
    public String toString() {
        return "EditMembershipRequest{"
                + "unofficialName='" + this.unofficialName + '\''
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
        EditMembershipRequest that = (EditMembershipRequest) o;
        return Objects.equals(this.unofficialName, that.unofficialName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.unofficialName);
    }
}
