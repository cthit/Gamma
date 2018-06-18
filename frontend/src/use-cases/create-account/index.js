import React, { Component } from "react";
import { Route } from "react-router-dom";
import InputDataAndCodeScreen from "./screens/input-data-and-code-screen";
import InputCidScreenContainer from "./container/InputCidScreenContainer";

class CreateAccount extends Component {
  render() {
    return (
      <div>
        <Route
          path={this.props.match.path + ""}
          exact
          component={InputCidScreenContainer}
        />
        <Route
          path={this.props.match.path + "/input"}
          exact
          component={InputDataAndCodeScreen}
        />
      </div>
    );
  }
}

export default CreateAccount;
