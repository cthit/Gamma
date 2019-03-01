import React from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllSuperGroups from "./screens/show-all-super-groups";
import ShowSuperGroupDetails from "./screens/show-super-group-details";
import CreateNewSuperGroup from "./screens/create-new-super-group";
import EditSuperGroup from "./screens/edit-super-group-details";

const SuperGroups = () => (
    <Switch>
        <Route
            path="/super-groups/:superGroupId/edit"
            component={EditSuperGroup}
        />
        <Route path="/super-groups/add" component={CreateNewSuperGroup} />
        <Route
            path="/super-groups/:superGroupId"
            component={ShowSuperGroupDetails}
        />
        <Route path="/super-groups" component={ShowAllSuperGroups} />
    </Switch>
);

export default SuperGroups;
