import React from "react";
import { Switch, Route } from "react-router-dom";
import GroupsCrud from "./groups-crud";
import GroupsAvatar from "./groups-avatar";
import GroupsBanner from "./groups-banner";

const Groups = () => {
    return (
        <Switch>
            <Route exact path={"/groups/:id/banner"} component={GroupsBanner} />
            <Route exact path={"/groups/:id/avatar"} component={GroupsAvatar} />
            <Route component={GroupsCrud} />
        </Switch>
    );
};

export default Groups;
