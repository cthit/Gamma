import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllClients from "./screens/show-all-clients";
import ShowClientDetails from "./screens/show-client-details";
class Clients extends Component {
    render() {
        return (
            <Switch>
                <Route path={"/clients"} exact component={ShowAllClients} />
                <Route path={"/clients/:id"} exact component={ShowClientDetails} />
            </Switch>
        );
    }
}

export default Clients;
