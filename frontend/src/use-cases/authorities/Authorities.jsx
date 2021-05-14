import React from "react";
import { Route, Switch } from "react-router-dom";

import CreateAuthorityLevel from "./screens/create-authority-level";
import AddToAuthorityLevel from "./screens/edit-authority";
import ViewAuthorities from "./screens/view-authorities";

const Authorities = () => {
    return (
        <Switch>
            <Route
                exact
                path={"/authorities/edit/:id"}
                component={AddToAuthorityLevel}
            />
            <Route
                exact
                path={"/authorities/create-authority-level"}
                component={CreateAuthorityLevel}
            />
            <Route component={ViewAuthorities} />
        </Switch>
    );
};

export default Authorities;
