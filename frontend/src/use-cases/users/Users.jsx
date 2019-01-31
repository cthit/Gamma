import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import { Route, Switch } from "react-router-dom";
import EditUserDetails from "./screens/edit-user-details";
import ShowAllUsers from "./screens/show-all-users";
import ShowUserDetails from "./screens/show-user-details";

const Users = () => (
    <DigitLayout.Fill>
        <Switch>
            <Route path="/users" exact component={ShowAllUsers} />
            <Route path="/users/:id" exact component={ShowUserDetails} />
            <Route path="/users/:id/edit" exact component={EditUserDetails} />
        </Switch>
    </DigitLayout.Fill>
);

export default Users;
