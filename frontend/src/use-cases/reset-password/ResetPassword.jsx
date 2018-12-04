import React from "react";

import ResetPasswordInitalize from "./screens/reset-password-initalize";
import ResetPasswordFinish from "./screens/reset-password-finish";

class ResetPassword extends React.Component {
    constructor(props) {
        super();

        props.gammaLoadingFinished();
    }

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
