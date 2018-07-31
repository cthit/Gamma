import React from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllActivationCodes from "./screens/show-all-activation-codes";
import ShowActivationCodeDetails from "./screens/show-activation-code-details";
import EditActivationCodeDetails from "./screens/edit-activation-code-details";

class ActivationCodes extends React.Component {
  constructor(props) {
    super();

    props.activationCodesLoad();
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
