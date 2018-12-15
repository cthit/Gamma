import React from "react";

import { Route, Switch } from "react-router-dom";

import ShowAllGroups from "./screens/show-all-groups";
import CreateNewGroup from "./screens/create-new-group";
import EditGroupDetails from "./screens/edit-group-details";
import ShowGroupDetails from "./screens/show-group-details";
import AddUsersToGroup from "./screens/add-users-to-group";
import EditUsersInGroup from "./screens/edit-users-in-group";

const Groups = () => (
    <Switch>
        <Route path="/groups" exact component={ShowAllGroups}/>
        <Route path="/groups/new" exact component={CreateNewGroup}/>
        <Route
            path="/groups/:id/edit"
            exact
            component={EditGroupDetails}
        />
        <Route
            path="/groups/:id/members"
            exact
            component={AddUsersToGroup}
        />
        <Route path="/groups/:id" exact component={ShowGroupDetails}/>
        <Route
            path="/groups/:id/users"
            exace
            component={EditUsersInGroup}
        />
    </Switch>
)

export default Groups;
