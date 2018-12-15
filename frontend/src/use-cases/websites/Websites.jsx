import React from "react";

import { Route, Switch } from "react-router-dom";

import ShowAllWebsites from "./screens/show-all-websites";
import AddNewWebsite from "./screens/add-new-website";
import EditWebsiteDetails from "./screens/edit-website-details";
import ShowWebsiteDetails from "./screens/show-website-details";

const Websites = () => (
    <Switch>
        <Route path="/websites" exact component={ShowAllWebsites} />
        <Route path="/websites/add" exact component={AddNewWebsite} />
        <Route path="/websites/:id/edit" exact component={EditWebsiteDetails} />
        <Route path="/websites/:id" exact component={ShowWebsiteDetails} />
    </Switch>
);

export default Websites;
