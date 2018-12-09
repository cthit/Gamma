import React from "react";
import { Switch, Route } from "react-router-dom";

import ShowWhitelist from "./screens/show-whitelist";
import AddToWhitelist from "./screens/add-to-whitelist";
import ShowWhitelistItem from "./screens/show-whitelist-item";
import EditWhitelistItemDetails from "./screens/edit-whitelist-item-details";
import ValidateCid from "./screens/validate-cid";

class Whitelist extends React.Component {
    render() {
        return (
            <Switch>
                <Route path="/whitelist" exact component={ShowWhitelist} />
                <Route
                    path="/whitelist/validate"
                    exact
                    component={ValidateCid}
                />
                <Route path="/whitelist/add" exact component={AddToWhitelist} />
                <Route
                    path="/whitelist/:id/edit"
                    exact
                    component={EditWhitelistItemDetails}
                />
                <Route
                    path="/whitelist/:id"
                    exact
                    component={ShowWhitelistItem}
                />
            </Switch>
        );
    }
}

export default Whitelist;
