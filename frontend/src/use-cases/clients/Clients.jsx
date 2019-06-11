import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllClients from "./screens/show-all-clients";
import ShowClientDetails from "./screens/show-client-details";
const Clients = () => (
    <DigitLayout.Fill>
            <Switch>
                <Route path={"/clients"} exact component={ShowAllClients} />
                <Route path={"/clients/:id"} exact component={ShowClientDetails} />
            </Switch>
    </DigitLayout.Fill>
        );

export default Clients;
