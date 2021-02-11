package it.chalmers.gamma.bootstrap.mock;

import java.util.List;

public class MockData {

    private List<MockUser> users;
    private List<MockGroup> groups;
    private List<MockSuperGroup> superGroups;
    private List<MockPost> posts;

    public List<MockUser> getUsers() {
        return this.users;
    }

    public void setUsers(List<MockUser> users) {
        this.users = users;
    }

    public List<MockGroup> getGroups() {
        return this.groups;
    }

    public void setGroups(List<MockGroup> groups) {
        this.groups = groups;
    }

    public List<MockSuperGroup> getSuperGroups() {
        return this.superGroups;
    }

    public void setSuperGroups(List<MockSuperGroup> superGroups) {
        this.superGroups = superGroups;
    }

    public List<MockPost> getPosts() {
        return this.posts;
    }

    public void setPosts(List<MockPost> posts) {
        this.posts = posts;
    }
}
