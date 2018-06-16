import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";
import CreateAccount from "./use-cases/create-account";

class App extends Component {
  render() {
    return (
      <div>
        <Switch>
          <Route path="/create-account" component={CreateAccount} />
        </Switch>
      </div>
    );
  }
}

export default App;
