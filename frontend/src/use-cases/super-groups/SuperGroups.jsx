import React from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllSuperGroups from "./screens/show-all-super-groups";

const SuperGroups = () => (
    <Switch>
        <Route path="/" component={ShowAllSuperGroups} />
    </Switch>
);

export default SuperGroups;
