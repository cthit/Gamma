import React from "react";
import { Switch, Route } from "react-router-dom";

import ShowWhitelist from "./screens/show-whitelist";
import AddNewWhitelistItem from "./screens/add-new-whitelist-item";
import ShowWhitelistItem from "./screens/show-whitelist-item";
import EditWhitelistItemDetails from "./screens/edit-whitelist-item-details";

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
        <Route
          path="/whitelist/:id/edit"
          exact
          component={EditWhitelistItemDetails}
        />
        <Route path="/whitelist/:id" exact component={ShowWhitelistItem} />
      </Switch>
    );
  }
}

export default Whitelist;
