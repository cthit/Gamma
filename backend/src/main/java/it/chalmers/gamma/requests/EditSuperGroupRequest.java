package it.chalmers.gamma.requests;

import java.util.Objects;

import javax.validation.constraints.Size;

public class EditSuperGroupRequest {

    @Size(max = 50, message = "PRETTY_NAME_TOO_LONG")
    private String prettyName;

    public String getPrettyName() {
        return this.prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EditSuperGroupRequest that = (EditSuperGroupRequest) o;
        return Objects.equals(this.prettyName, that.prettyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.prettyName);
    }

    @Override
    public String toString() {
        return "EditSuperGroupRequest{"
                + ", prettyName='" + this.prettyName + '\''
                + '}';
    }

}
