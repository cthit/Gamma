import React from "react";

import { Switch, Route } from "react-router-dom";

import ShowAllWebsites from "./screens/show-all-websites";
import AddNewWebsite from "./screens/add-new-website";

class Websites extends React.Component {
  constructor(props) {
    super();

    props.websitesLoad();
  }

  render() {
    return (
      <Switch>
        <Route path="/websites" exact component={ShowAllWebsites} />
        <Route path="/websites/add" exact component={AddNewWebsite} />
      </Switch>
    );
  }
}

export default Websites;
