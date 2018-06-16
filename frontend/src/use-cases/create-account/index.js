import React, { Component } from "react";
import { Route } from "react-router-dom";
import { InputCidScreen } from "./screens/input-cid-screen";
import { InputDataAndCodeScreen } from "./screens/input-data-and-code-screen";

class CreateAccount extends Component {
  render() {
    return (
      <div>
        <Route
          path={this.props.match.path + ""}
          exact
          component={InputCidScreen}
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
