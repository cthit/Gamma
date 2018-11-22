import React from "react";
import { Route, Switch } from "react-router-dom";
import EditActivationCodeDetails from "./screens/edit-activation-code-details";
import ShowActivationCodeDetails from "./screens/show-activation-code-details";
import ShowAllActivationCodes from "./screens/show-all-activation-codes";

class ActivationCodes extends React.Component {
    constructor(props) {
        super();

        props.activationCodesLoad().then(response => {
            props.gammaLoadingFinished();
        });
    }

    render() {
        return (
            <Switch>
                <Route
                    path="/activation-codes"
                    exact
                    component={ShowAllActivationCodes}
                />
                <Route
                    path="/activation-codes/:id"
                    exact
                    component={ShowActivationCodeDetails}
                />
                <Route
                    path="/activation-codes/:id/edit"
                    exact
                    component={EditActivationCodeDetails}
                />
            </Switch>
        );
    }
}

export default ActivationCodes;
