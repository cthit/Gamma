package it.chalmers.gamma.response.user;

import it.chalmers.gamma.db.entity.ITUser;

import java.util.List;

public class GetITUsersResponse {

    private final List<ITUser> users;

    public GetITUsersResponse(List<ITUser> users) {
        this.users = users;
    }

    public List<ITUser> getUsers() {
        return users;
    }
}
