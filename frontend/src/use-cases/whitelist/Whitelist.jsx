import React from "react";
import { Switch, Route } from "react-router-dom";

import ShowWhitelist from "./screens/show-whitelist";

class Whitelist extends React.Component {
  constructor(props) {
    super();

    props.whitelistLoad();
  }

  render() {
    return (
      <Switch>
        <Route path="/whitelist" exact component={ShowWhitelist} />
      </Switch>
    );
  }
}

export default Whitelist;
