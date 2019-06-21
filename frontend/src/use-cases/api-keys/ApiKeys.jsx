import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllApiKeys from "./screens/show-all-api-keys";

const ApiKeys = () => (
    <DigitLayout.Fill>
        <Switch>
            <Route path={"/api_keys"} exact component={ShowAllApiKeys}/>
        </Switch>
    </DigitLayout.Fill>
);

export default ApiKeys;