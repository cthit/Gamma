import React from "react";
import { Switch, Route } from "react-router-dom";
import ResetPasswordInitalize from "./screens/reset-password-initalize";
import ResetPasswordFinish from "./screens/reset-password-finish";

class ResetPassword extends React.Component {

    render() {
        return (
            <Switch>
                <Route
                    path="/reset-password"
                    component={ResetPasswordInitalize}
                />
                <Route
                    path="/reset-password/finish/:token"
                    component={ResetPasswordFinish}
                />
            </Switch>
        );
    }
}

export default ResetPassword;