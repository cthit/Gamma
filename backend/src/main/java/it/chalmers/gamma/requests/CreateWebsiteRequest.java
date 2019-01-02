package it.chalmers.gamma.requests;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class CreateWebsiteRequest {

    @NotNull(message = "NAME_MUST_BE_PROVIDED")
    private String name;

    @NotNull(message = "PRETTY_NAME_MUST_BE_PROVIDED")
    private String prettyName;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

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
