import React, { Component } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { Switch, Route } from "react-router-dom";
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

import CreateAccount from "../use-cases/create-account";
import createAccountTranslations from "../use-cases/create-account/CreateAccount.translations.jsx";
import Demo from "../use-cases/demo";
import demoTranslations from "../use-cases/demo/Demo.translations.json";

import commonTranslations from "../common/utils/translations/CommonTranslations.json";

import { Padding, Spacing } from "../common-ui/layout";

class App extends Component {
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
        </List>
      </div>
    );

    const { mobileOpen } = this.state;

    return (
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
              <GammaRedirect />
              <GammaToast />
              <Switch>
                <Route path="/create-account" component={CreateAccount} />
                <Route path="/demo" component={Demo} />
              </Switch>
            </Padding>
          </StyledMain>
        </StyledRoot>
      </div>
    );
  }
}

export default withLocalize(App);
