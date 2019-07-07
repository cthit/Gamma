import React from "react";
import { Switch, Route } from "react-router-dom";
import ResetPasswordInitialize from "./screens/reset-password-initalize";
import ResetPasswordFinish from "./screens/reset-password-finish";

class ResetPassword extends React.Component {

    render() {
        return (
            <Switch>
                <Route
                    path="/reset-password/finish"
                    component={ResetPasswordFinish}
                />
                <Route
                    path="/reset-password"
                    component={ResetPasswordInitialize}
                />
            </Switch>
        );
    }
}

export default ResetPassword;