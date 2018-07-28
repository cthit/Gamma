import React from "react";

import { Switch, Route } from "react-router-dom";

import ShowAllWebsites from "./screens/show-all-websites";

class Websites extends React.Component {
  constructor(props) {
    super();

    props.websitesLoad();
  }

  render() {
    return (
      <Switch>
        <Route path="/websites" component={ShowAllWebsites} />
      </Switch>
    );
  }
}

export default Websites;
