import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import { Route, Switch } from "react-router-dom";
import EditUserDetails from "./screens/edit-user-details";
import ShowAllUsers from "./screens/show-all-users";
import ShowUserDetails from "./screens/show-user-details";
import CreateNewUser from "./screens/create-new-user";
import ChangeUserPassword from "./screens/change-user-password";

const Users = () => (
    <DigitLayout.Fill>
        <Switch>
            <Route path="/users/new" exact component={CreateNewUser} />
            <Route path="/users" exact component={ShowAllUsers} />
            <Route path="/users/:id" exact component={ShowUserDetails} />
            <Route path="/users/:id/edit" exact component={EditUserDetails} />
            <Route
                path="/users/:id/change_password"
                exact
                component={ChangeUserPassword}
            />
        </Switch>
    </DigitLayout.Fill>
);

export default Users;
