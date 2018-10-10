package it.chalmers.gamma.requests;

public class DeleteGroupRequest {
    String group;
    String adminUser;
    String adminPassword;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
