import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";
import { DrawerNavigationLink } from "./elements/drawer-navigation-link";
import { Padding, Spacing } from "../common-ui/layout";
import GammaRedirectContainer from "./containers/GammaRedirectContainer";
import GammaToastContainer from "./containers/GammaToastContainer";

import { List, Typography, Hidden, Toolbar } from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";

import {
  StyledRoot,
  StyledAppBar,
  StyledMenuButton,
  StyledDrawer,
  StyledMain
} from "./styles";
import { UserInformation } from "./elements/user-information";
import CreateAccountContainer from "../use-cases/create-account/container/CreateAccountContainer";
import { Demo } from "../use-cases/demo";

class App extends Component {
  state = {
    mobileOpen: false
  };

  handleDrawerToggle = () => {
    this.setState({ mobileOpen: !this.state.mobileOpen });
  };

  render() {
    const title = "Gamma - IT-konto";

    const drawer = (
      <div>
        <Spacing />
        <List component="nav">
          <DrawerNavigationLink link="/demo">Demo</DrawerNavigationLink>
          <DrawerNavigationLink link="/create-account">
            Skapa konto
          </DrawerNavigationLink>
        </List>
      </div>
    );
    return (
      <div>
        <StyledRoot>
          <StyledAppBar>
            <Toolbar>
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
            </Toolbar>
          </StyledAppBar>
          <Hidden mdUp>
            <StyledDrawer
              variant="temporary"
              anchor="left"
              open={this.state.mobileOpen}
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
              <GammaRedirectContainer />
              <GammaToastContainer />
              <Switch>
                <Route
                  path="/create-account"
                  component={CreateAccountContainer}
                />
                <Route path="/demo" component={Demo} />
              </Switch>
            </Padding>
          </StyledMain>
        </StyledRoot>
      </div>
    );
  }
}

export default App;
