import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllApiKeys from "./screens/show-all-api-keys";
import ShowApiKeyDetails from "./screens/show-api-key-details";
import AddNewApiKey from "./screens/add-new-api-key";
const ApiKeys = () => (
    <DigitLayout.Fill>
        <Switch>
            <Route path={"/access_keys"} exact component={ShowAllApiKeys} />
            <Route path={"/access_keys/new"} exact component={AddNewApiKey} />
            <Route
                path={"/access_keys/:id"}
                exact
                component={ShowApiKeyDetails}
            />
        </Switch>
    </DigitLayout.Fill>
);

export default ApiKeys;
