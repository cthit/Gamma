import React from "react";
import { Route, Switch } from "react-router-dom";
import MyAccount from "./views/my-account";
import MyGroups from "./views/my-groups";

const Me = () => (
    <Switch>
        <Route path={"/me/groups"} exact component={MyGroups} />
        <Route path={"/me"} exact component={MyAccount} />
    </Switch>
);

export default Me;
