import React from "react";
import { Switch, Route } from "react-router-dom";
import MeChangePassword from "./screens/me-change-password";
import MeGroups from "./screens/me-groups";
import MeAvatar from "./screens/me-avatar";
import MeApprovals from "./screens/me-approvals";
import MeCRUD from "./screens/me-crud";

const Me = () => {
    return (
        <Switch>
            <Route exact path={"/me/groups"} component={MeGroups} />
            <Route exact path={"/me/avatar"} component={MeAvatar} />
            <Route
                exact
                path={"/me/change-password"}
                component={MeChangePassword}
            />
            <Route exact path="/me/approvals" component={MeApprovals} />
            <Route component={MeCRUD} />
        </Switch>
    );
};

export default Me;
