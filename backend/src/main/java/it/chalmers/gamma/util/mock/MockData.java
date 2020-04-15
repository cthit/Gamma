package it.chalmers.gamma.util.mock;

import java.util.List;

public class MockData {

    private List<MockITUser> users;
    private List<MockFKITGroup> groups;
    private List<MockFKITSuperGroup> superGroups;
    private List<MockPost> posts;

    public List<MockITUser> getUsers() {
        return this.users;
    }

    public void setUsers(List<MockITUser> users) {
        this.users = users;
    }

    public List<MockFKITGroup> getGroups() {
        return this.groups;
    }

    public void setGroups(List<MockFKITGroup> groups) {
        this.groups = groups;
    }

    public List<MockFKITSuperGroup> getSuperGroups() {
        return this.superGroups;
    }

    public void setSuperGroups(List<MockFKITSuperGroup> superGroups) {
        this.superGroups = superGroups;
    }

    public List<MockPost> getPosts() {
        return this.posts;
    }

    public void setPosts(List<MockPost> posts) {
        this.posts = posts;
    }
}
