import React, { Component } from "react";
import { Route } from "react-router-dom";
import InputDataAndCodeScreenContainer from "./container/InputDataAndCodeScreenContainer";
import InputCidScreenContainer from "./container/InputCidScreenContainer";
import CreationOfAccountFinishedScreen from "./screens/creation-of-account-finished-screen";
import { Fill } from "../../common-ui/layout";

class CreateAccount extends Component {
  render() {
    return (
      <Fill>
        <Route
          path={this.props.match.path + ""}
          exact
          component={InputCidScreenContainer}
        />
        <Route
          path={this.props.match.path + "/input"}
          exact
          component={InputDataAndCodeScreenContainer}
        />
        <Route
          path={this.props.match.path + "/finished"}
          exact
          component={CreationOfAccountFinishedScreen}
        />
      </Fill>
    );
  }
}

export default CreateAccount;
