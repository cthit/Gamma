import React from "react";
import { Route, Switch } from "react-router-dom";
import ViewAuthorities from "./screens/view-authorities";
import CreateAuthorityLevel from "./screens/create-authority-level";
import AddToAuthorityLevel from "./screens/add-to-authority-level";

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
