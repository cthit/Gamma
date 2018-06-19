import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";
import CreateAccount from "../use-cases/create-account";
import { Drawer } from "./elements/drawer";
import { Header } from "./elements/header";
import { NavLink } from "react-router-dom";
import { Layout } from "./elements/layout";
import { PageContent } from "./styles";

class App extends Component {
  //Makes sure that MDL is loaded correctly. See: http://quaintous.com/2015/07/09/react-components-with-mdl/
  componentDidUpdate() {
    window.componentHandler.upgradeDom();
  }

  render() {
    const title = "Gamma";

    const drawer = (
      <div>
        <NavLink className="mdl-navigation__link" to="/create-account">
          Create-account
        </NavLink>
        <NavLink className="mdl-navigation__link" to="/create-account/input">
          Create-account/input
        </NavLink>
      </div>
    );
    return (
      <div>
        <Layout>
          <Header drawer={drawer} title={title} />
          <Drawer title={title}>{drawer}</Drawer>
          <main className="mdl-layout__content">
            <PageContent>
              <Switch>
                <Route path="/create-account" component={CreateAccount} />
              </Switch>
            </PageContent>
          </main>
        </Layout>
      </div>
    );
  }
}

export default App;
