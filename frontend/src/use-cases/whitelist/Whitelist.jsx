import React from "react";
import { Switch, Route } from "react-router-dom";

import ShowWhitelist from "./screens/show-whitelist";
import AddNewWhitelistItem from "./screens/add-new-whitelist-item";

class Whitelist extends React.Component {
  constructor(props) {
    super();

    props.whitelistLoad();
  }

  render() {
    return (
      <Switch>
        <Route path="/whitelist" exact component={ShowWhitelist} />
        <Route path="/whitelist/add" exact component={AddNewWhitelistItem} />
      </Switch>
    );
  }
}

export default Whitelist;
