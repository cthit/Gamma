import React from "react";
import { Route, Switch } from "react-router-dom";
import ResetPasswordInitialize from "./screens/reset-password-initalize";
import ResetPasswordFinish from "./screens/reset-password-finish";

const ResetPassword = () => {
    return (
        <Switch>
            <Route
                exact
                path="/reset-password/finish"
                component={ResetPasswordFinish}
            />
            <Route
                exact
                path="/reset-password"
                component={ResetPasswordInitialize}
            />
        </Switch>
    );
};

export default ResetPassword;
