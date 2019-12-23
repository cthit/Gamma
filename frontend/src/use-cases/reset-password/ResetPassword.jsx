import React from "react";
import { Route, Switch } from "react-router-dom";
import ResetPasswordInitialize from "./screens/reset-password-initalize";
import ResetPasswordFinish from "./screens/reset-password-finish";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";

const ResetPassword = () => {
    return (
        <Switch>
            <Route
                path="/reset-password/finish"
                component={ResetPasswordFinish}
            />
            <Route path="/reset-password" component={ResetPasswordInitialize} />
        </Switch>
    );
};

export default ResetPassword;
