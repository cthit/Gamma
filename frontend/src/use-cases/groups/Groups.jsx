import React from "react";

import { Switch, Route } from "react-router-dom";

import CreateNewGroup from "./screens/create-new-group";

class Groups extends React.Component {
  constructor(props) {
    super();
  }

  render() {
    return (
      <Switch>
        <Route path="/groups/new" component={CreateNewGroup} />
      </Switch>
    );
  }
}

export default Groups;
