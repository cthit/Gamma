import React, { Component } from "react";
import { Route } from "react-router-dom";
import InputDataAndCodeScreenContainer from "./container/InputDataAndCodeScreenContainer";
import InputCidScreenContainer from "./container/InputCidScreenContainer";

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
      </Fill>
    );
  }
}

export default CreateAccount;
