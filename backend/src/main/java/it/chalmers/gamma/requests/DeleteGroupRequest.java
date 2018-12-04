package it.chalmers.gamma.requests;

import java.util.Objects;

public class DeleteGroupRequest {
    String group;
    String adminUser;
    String adminPassword;

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAdminUser() {
        return this.adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPassword() {
        return this.adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeleteGroupRequest that = (DeleteGroupRequest) o;
        return this.group.equals(that.group)
            && this.adminUser.equals(that.adminUser)
            && this.adminPassword.equals(that.adminPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.group, this.adminUser, this.adminPassword);
    }

    @Override
    public String toString() {
        return "DeleteGroupRequest{"
            + "group='" + this.group + '\''
            + ", adminUser='" + this.adminUser + '\''
            + ", adminPassword='" + this.adminPassword + '\''
            + '}';
    }
}
