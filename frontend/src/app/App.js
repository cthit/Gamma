import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";
import CreateAccount from "../use-cases/create-account";
import { Drawer } from "./elements/drawer";
import { Header } from "./elements/header";
import { DrawerNavigationLink } from "./elements/drawer-navigation-link";
import { NavLink } from "react-router-dom";
import { Layout } from "./elements/layout";
import { PageContent } from "./styles";
import { Padding } from "../common-ui/layout";
import GammaRedirectContainer from "./containers/GammaRedirectContainer";
import GammaToastContainer from "./containers/GammaToastContainer";

class App extends Component {
  //Makes sure that MDL is loaded correctly. See: http://quaintous.com/2015/07/09/react-components-with-mdl/
  componentDidUpdate() {
    window.componentHandler.upgradeDom();
  }

  componentDidMount() {
    document.querySelector(".mdl-layout__drawer").addEventListener(
      "click",
      function() {
        document
          .querySelector(".mdl-layout__obfuscator")
          .classList.remove("is-visible");
        this.classList.remove("is-visible");
      },
      false
    );
  }

  render() {
    const title = "Gamma - IT-konto";

    const drawer = (
      <div>
        <DrawerNavigationLink link="/create-account">
          Create-account
        </DrawerNavigationLink>
        <DrawerNavigationLink link="/create-account/email-sent">
          Create-account/email-sent
        </DrawerNavigationLink>
        <DrawerNavigationLink link="/create-account/input">
          Create-account/input
        </DrawerNavigationLink>
        <DrawerNavigationLink link="/create-account/finished">
          Create-account/finished
        </DrawerNavigationLink>
      </div>
    );
    return (
      <div>
        <Layout>
          <Header drawer={drawer} title={title} />
          <Drawer title={title}>{drawer}</Drawer>
          <main className="mdl-layout__content">
            <PageContent>
              <Padding>
                <GammaRedirectContainer />
                <GammaToastContainer />
                <Switch>
                  <Route path="/create-account" component={CreateAccount} />
                </Switch>
              </Padding>
            </PageContent>
          </main>
        </Layout>
      </div>
    );
  }
}

export default App;
