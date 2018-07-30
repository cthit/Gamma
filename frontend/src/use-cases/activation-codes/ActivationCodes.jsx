import React from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllActivationCodes from "./screens/show-all-activation-codes";

class ActivationCodes extends React.Component {
  constructor(props) {
    super();

    props.activationCodesLoad();
  }

  render() {
    return (
      <Switch>
        <Route path="/activation-codes" component={ShowAllActivationCodes} />
      </Switch>
    );
  }
}

export default ActivationCodes;
