import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import { Route, Switch } from "react-router-dom";
import EditUserDetails from "./screens/edit-user-details";
import ShowAllUsers from "./screens/show-all-users";
import ShowUserDetails from "./screens/show-user-details";

class Users extends React.Component {
    constructor(props) {
        super();

        props.usersLoad().then(response => {
            props.gammaLoadingFinished();
        });
    }

    render() {
        return (
            <DigitLayout.Fill>
                <Switch>
                    <Route path="/users" exact component={ShowAllUsers} />
                    <Route
                        path="/users/:cid"
                        exact
                        component={ShowUserDetails}
                    />
                    <Route
                        path="/users/:cid/edit"
                        exact
                        component={EditUserDetails}
                    />
                </Switch>
            </DigitLayout.Fill>
        );
    }
}

export default Users;
