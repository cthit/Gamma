package it.chalmers.gamma.requests;

import java.time.Year;
import java.util.Objects;

public class AddUserGroupRequest {
    private String user;
    private String post;
    private String unofficialName;
    private Year year;

    public Year getYear() {
        return this.year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPost() {
        return this.post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUnofficialName() {
        return this.unofficialName;
    }

    public void setUnofficialName(String unofficialName) {
        this.unofficialName = unofficialName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddUserGroupRequest that = (AddUserGroupRequest) o;
        return this.user.equals(that.user)
            && this.post.equals(that.post)
            && this.unofficialName.equals(that.unofficialName)
            && this.year.equals(that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.user, this.post, this.unofficialName, this.year);
    }

    @Override
    public String toString() {
        return "AddUserGroupRequest{"
            + "user='" + this.user + '\''
            + ", post='" + this.post + '\''
            + ", unofficialName='" + this.unofficialName + '\''
            + ", year=" + this.year
            + '}';
    }
}
