import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";
import ShowAllClients from "./screens/show-all-clients";

class Clients extends Component {
    render() {
        return (
            <Switch>
                <Route paht={"/clients"} component={ShowAllClients} />
            </Switch>
        );
    }
}

export default Clients;
