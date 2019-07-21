import React from "react";
import { Route, Switch } from "react-router-dom";
import MyAccount from "./views/my-account";
import MyGroups from "./views/my-groups";
import ChangeMyPassword from "./views/my-account/screens/change-my-password";

const Me = () => (
    <Switch>
        <Route path={"/me/groups"} exact component={MyGroups} />
        <Route
            path={"/me/change_password"}
            exact
            component={ChangeMyPassword}
        />
        <Route path={"/me"} exact component={MyAccount} />
    </Switch>
);

export default Me;
