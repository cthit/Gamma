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

import Users from "../use-cases/users";
import usersTranslations from "../use-cases/users/Users.translations.jsx";

import commonTranslations from "../common/utils/translations/CommonTranslations.json";

import { Padding, Spacing, Fill, Center } from "../common-ui/layout";
import { ProvidersForApp } from "./ProvidersForApp";
import IfElseRendering from "../common/declaratives/if-else-rendering";
import GammaLinearProgress from "../common/elements/gamma-linear-progress";
import { Title, Text } from "../common-ui/text";
import ContainUserToAllowedPages from "../common/declaratives/contain-user-to-allowed-pages";
import Error from "../use-cases/error";

import Posts from "../use-cases/posts";
import postsTranslations from "../use-cases/posts/Posts.translations.jsx";

import Whitelist from "../use-cases/whitelist";
import whitelistTranslations from "../use-cases/whitelist/Whitelist.translations.jsx";

import Groups from "../use-cases/groups";
import groupsTranslations from "../use-cases/groups/Groups.translations.jsx";

import Websites from "../use-cases/websites";
import websitesTranslations from "../use-cases/websites/Websites.translations.jsx";

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

    console.log(postsTranslations);

    props.addTranslation({
      ...appTranslations,
      ...commonTranslations,
      ...createAccountTranslations,
      ...loginTranslations,
      ...demoTranslations,
      ...usersTranslations,
      ...postsTranslations,
      ...whitelistTranslations,
      ...groupsTranslations,
      ...websitesTranslations
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
          <DrawerNavigationLink onClick={this._closeDrawer} link="/users">
            Users
          </DrawerNavigationLink>
          <DrawerNavigationLink onClick={this._closeDrawer} link="/groups">
            Groups
          </DrawerNavigationLink>
          <DrawerNavigationLink onClick={this._closeDrawer} link="/posts">
            Posts
          </DrawerNavigationLink>
          <DrawerNavigationLink onClick={this._closeDrawer} link="/whitelist">
            Whitelist
          </DrawerNavigationLink>
          <DrawerNavigationLink onClick={this._closeDrawer} link="/websites">
            Websites
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
                    <Route path="/users" component={Users} />
                    <Route path="/groups" component={Groups} />
                    <Route path="/create-account" component={CreateAccount} />
                    <Route path="/login" component={Login} />
                    <Route path="/whitelist" component={Whitelist} />
                    <Route path="/error" component={Error} />
                    <Route path="/demo" component={Demo} />
                    <Route path="/posts" component={Posts} />
                    <Route path="/websites" component={Websites} />
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
