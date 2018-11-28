package it.chalmers.gamma.requests;

import java.util.Objects;

public class CreateWebsiteRequest {
    private String name;
    private String prettyName;

    public String getPrettyName() {
        return this.prettyName;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateWebsiteRequest that = (CreateWebsiteRequest) o;
        return this.name.equals(that.name)
            && this.prettyName.equals(that.prettyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.prettyName);
    }

    @Override
    public String toString() {
        return "CreateWebsiteRequest{"
            + "name='" + this.name + '\''
            + ", prettyName='" + this.prettyName + '\''
            + '}';
    }
}
