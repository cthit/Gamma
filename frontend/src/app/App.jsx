import React, { Component } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { Switch, Route, BrowserRouter } from "react-router-dom";
import { List, Typography, Hidden } from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";
import { withLocalize } from "react-localize-redux";

import {
  StyledRoot,
  StyledAppBar,
  StyledMenuButton,
  StyledDrawer,
  StyledMain,
  StyledToolbar
} from "./App.styles";

import GammaRedirect from "./views/gamma-redirect";
import GammaToast from "./views/gamma-toast";

import DrawerNavigationLink from "./elements/drawer-navigation-link";
import UserInformation from "./elements/user-information";

import SignIn from "../use-cases/sign-in";
import signInTranslations from "../use-cases/sign-in/SignIn.translations.jsx";

import CreateAccount from "../use-cases/create-account";
import createAccountTranslations from "../use-cases/create-account/CreateAccount.translations.jsx";

import Demo from "../use-cases/demo";
import demoTranslations from "../use-cases/demo/Demo.translations.json";

import Home from "../use-cases/home";

import commonTranslations from "../common/utils/translations/CommonTranslations.json";

import TryToRedirect from "../common/declaratives/try-to-redirect";
import IfElseRender from "../common/declaratives/if-else-rendering";

import { Padding, Spacing } from "../common-ui/layout";
import { ProvidersForApp } from "./ProvidersForApp";

export class App extends Component {
  state = {
    mobileOpen: false
  };

  constructor(props) {
    super(props);

    props.initialize({
      languages: [
        { name: "English", code: "en" },
        { name: "Swedish", code: "sv" }
      ],
      options: {
        renderToStaticMarkup,
        renderInnerHtml: true,
        defaultLanguage: "en"
      }
    });

    props.addTranslation({
      ...commonTranslations,
      ...createAccountTranslations,
      ...signInTranslations,
      ...demoTranslations
    });
  }

  handleDrawerToggle = () => {
    this.setState({ mobileOpen: !this.state.mobileOpen });
  };

  _closeDrawer = () => {
    this.setState({
      mobileOpen: false
    });
  };

  render() {
    const title = "Gamma - IT-konto";

    const drawer = (
      <div>
        <Spacing />
        <List component="nav">
          <UserInformation />
          <DrawerNavigationLink onClick={this._closeDrawer} link="/demo">
            Demo
          </DrawerNavigationLink>
          <DrawerNavigationLink
            onClick={this._closeDrawer}
            link="/create-account"
          >
            Skapa konto
          </DrawerNavigationLink>
          <DrawerNavigationLink onClick={this._closeDrawer} link="/sign-in">
            Sign in
          </DrawerNavigationLink>
        </List>
      </div>
    );

    const { mobileOpen } = this.state;

    return (
      <BrowserRouter>
        <div>
          <StyledRoot>
            <StyledAppBar>
              <StyledToolbar>
                <StyledMenuButton
                  color="inherit"
                  aria-label="open drawer"
                  onClick={this.handleDrawerToggle}
                >
                  <MenuIcon />
                </StyledMenuButton>
                <Typography variant="title" color="inherit" noWrap>
                  {title}
                </Typography>
              </StyledToolbar>
            </StyledAppBar>
            <Hidden mdUp>
              <StyledDrawer
                variant="temporary"
                anchor="left"
                open={mobileOpen}
                onClose={this.handleDrawerToggle}
                ModalProps={{
                  keepMounted: true // Better open performance on mobile.
                }}
              >
                {drawer}
              </StyledDrawer>
            </Hidden>
            <Hidden smDown implementation="css">
              <StyledDrawer variant="permanent" open>
                {drawer}
              </StyledDrawer>
            </Hidden>
            <StyledMain>
              <Padding>
                {console.log(this.state)}
                <GammaRedirect />
                <GammaToast />
                <Switch>
                  <Route path="/home" component={Home} />
                  <Route path="/create-account" component={CreateAccount} />
                  <Route path="/sign-in" component={SignIn} />
                  <Route path="/demo" component={Demo} />
                  <Route
                    path="/"
                    render={props => (
                      <IfElseRender
                        test={false}
                        ifRender={() => (
                          <TryToRedirect
                            from="/"
                            to="/home"
                            currentPath={props.location.pathname}
                          />
                        )}
                        elseRender={() => (
                          <TryToRedirect
                            from="/"
                            to="/login"
                            currentPath={props.location.pathname}
                          />
                        )}
                      />
                    )}
                  />
                </Switch>
              </Padding>
            </StyledMain>
          </StyledRoot>
        </div>
      </BrowserRouter>
    );
  }
}

export default withLocalize(App);
