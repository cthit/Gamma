import React, { Component } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { Switch, Route, BrowserRouter, Redirect } from "react-router-dom";
import { List, Typography, Hidden } from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";
import { withLocalize } from "react-localize-redux";

import {
  StyledRoot,
  StyledAppBar,
  StyledMenuButton,
  StyledDrawer,
  StyledMain,
  StyledToolbar,
  HorizontalFill,
  GammaTitle
} from "./App.styles";

import appTranslations from "./App.translations.json";

import GammaRedirect from "./views/gamma-redirect";
import GammaToast from "./views/gamma-toast";

import DrawerNavigationLink from "./elements/drawer-navigation-link";
import UserInformation from "./elements/user-information";

import Login from "../use-cases/login";
import loginTranslations from "../use-cases/login/Login.translations.jsx";

import CreateAccount from "../use-cases/create-account";
import createAccountTranslations from "../use-cases/create-account/CreateAccount.translations.jsx";

import Demo from "../use-cases/demo";
import demoTranslations from "../use-cases/demo/Demo.translations.json";

import Home from "../use-cases/home";

import commonTranslations from "../common/utils/translations/CommonTranslations.json";

import { Padding, Spacing, Fill, Center } from "../common-ui/layout";
import { ProvidersForApp } from "./ProvidersForApp";
import IfElseRendering from "../common/declaratives/if-else-rendering";
import GammaLinearProgress from "../common/elements/gamma-linear-progress";
import { Title, Text } from "../common-ui/text";
import ContainUserToAllowedPages from "../common/declaratives/contain-user-to-allowed-pages";

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
        defaultLanguage: "sv"
      }
    });

    props.addTranslation({
      ...appTranslations,
      ...commonTranslations,
      ...createAccountTranslations,
      ...loginTranslations,
      ...demoTranslations
    });

    props.userUpdateMe();
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
          <DrawerNavigationLink onClick={this._closeDrawer} link="/home">
            Hem
          </DrawerNavigationLink>
          <DrawerNavigationLink
            onClick={this._closeDrawer}
            link="/create-account"
          >
            Skapa konto
          </DrawerNavigationLink>
          <DrawerNavigationLink onClick={this._closeDrawer} link="/login">
            Logga in
          </DrawerNavigationLink>
          <DrawerNavigationLink onClick={this._closeDrawer} link="/demo">
            Demo
          </DrawerNavigationLink>
        </List>
      </div>
    );

    const { mobileOpen } = this.state;

    var { loggedIn, loaded, text } = this.props;

    loggedIn = loggedIn != null ? loggedIn : false;
    loaded = loaded != null ? loaded : false;

    return (
      <BrowserRouter>
        <StyledRoot>
          <IfElseRendering
            test={!loggedIn && loaded}
            ifRender={() => (
              <Route
                render={props => (
                  <ContainUserToAllowedPages
                    currentPath={props.location.pathname}
                    allowedBasePaths={["/create-account"]}
                    to="/login"
                    toastTextOnRedirect={text.YouNeedToLogin}
                  />
                )}
              />
            )}
          />

          <StyledAppBar>
            <StyledToolbar>
              <StyledMenuButton
                color="inherit"
                aria-label="open drawer"
                onClick={this.handleDrawerToggle}
              >
                <MenuIcon />
              </StyledMenuButton>
              <HorizontalFill>
                <GammaTitle text={title} white />
                <Route
                  render={props => (
                    <UserInformation currentPath={props.location.pathname} />
                  )}
                />
              </HorizontalFill>
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
            <Route
              render={props => (
                <GammaRedirect currentPath={props.location.pathname} />
              )}
            />
            <GammaToast />
            <IfElseRendering
              test={loaded}
              elseRender={() => <GammaLinearProgress />}
              ifRender={() => (
                <Padding>
                  <Switch>
                    <Route path="/home" component={Home} />
                    <Route path="/create-account" component={CreateAccount} />
                    <Route path="/login" component={Login} />
                    <Route path="/demo" component={Demo} />
                    <Route
                      path="/"
                      render={props => (
                        <IfElseRendering
                          test={loggedIn}
                          ifRender={() => <Redirect to="/home" />}
                          elseRender={() => <Redirect to="/login" />}
                        />
                      )}
                    />
                  </Switch>
                </Padding>
              )}
            />
          </StyledMain>
        </StyledRoot>
      </BrowserRouter>
    );
  }
}

export default withLocalize(App);
