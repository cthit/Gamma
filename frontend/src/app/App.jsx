import React, { Component } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { Switch, Route, withRouter, Redirect } from "react-router-dom";
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
import GammaDialog from "./views/gamma-dialog";

import DrawerNavigationLink from "./elements/drawer-navigation-link";
import UserInformation from "./elements/user-information";

import Login from "../use-cases/login";
import CreateAccount from "../use-cases/create-account";
import Home from "../use-cases/home";
import Users from "../use-cases/users";
import Error from "../use-cases/error";
import Posts from "../use-cases/posts";
import Whitelist from "../use-cases/whitelist";
import Groups from "../use-cases/groups";
import Websites from "../use-cases/websites";
import ActivationCodes from "../use-cases/activation-codes";
import Gdpr from "../use-cases/gdpr";

import {
    Padding,
    Spacing,
    Fill,
    Center,
    Hide,
    HideFill
} from "../common-ui/layout";
import { ProvidersForApp } from "./ProvidersForApp";
import IfElseRendering from "../common/declaratives/if-else-rendering";
import GammaLinearProgress from "../common/elements/gamma-linear-progress";
import { Title, Text } from "../common-ui/text";
import ContainUserToAllowedPages from "../common/declaratives/contain-user-to-allowed-pages";
import GammaLoading from "./views/gamma-loading";

import {
    DigitHeader,
    DigitNavLink,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";

export class App extends Component {
    state = {
        mobileOpen: false,
        lastPath: "/"
    };

    constructor(props) {
        super(props);
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

    onRouteChanged(data) {
        if (data.location.pathname !== this.state.lastPath) {
            const lastBaseUrl = this.state.lastPath.split("/")[1];
            const currentBaseUrl = data.location.pathname.split("/")[1];

            if (lastBaseUrl !== currentBaseUrl) {
                this.props.gammaLoadingStart();
            }

            this.setState({
                lastPath: data.location.pathname
            });
        }
    }

    render() {
        const title = "Gamma - IT-konto";

        const drawer = closeDrawer => (
            <DigitLayout.Column padding="0">
                <DigitNavLink onClick={closeDrawer} text="Home" link="/home" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Create account"
                    link="/create-account"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Login"
                    link="/login"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Users"
                    link="/users"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Groups"
                    link="/groups"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Posts"
                    link="/posts"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Whitelist"
                    link="/whitelist"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Websites"
                    link="/websites"
                />
                <DigitNavLink onClick={closeDrawer} text="Gdpr" link="/gdpr" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Activation codes"
                    link="/activation-codes"
                />
            </DigitLayout.Column>
        );

        const header = () => (
            <Route
                render={props => (
                    <UserInformation currentPath={props.location.pathname} />
                )}
            />
        );

        const { mobileOpen } = this.state;

        var { loggedIn, userLoaded, loading } = this.props;

        loggedIn = loggedIn != null ? loggedIn : false;
        userLoaded = userLoaded != null ? userLoaded : false;

        return (
            <DigitTranslations
                uniquePath="App"
                translations={appTranslations}
                render={text => (
                    <DigitHeader
                        title={title}
                        renderDrawer={drawer}
                        renderHeader={header}
                        renderMain={() => (
                            <div>
                                <Route
                                    render={props => (
                                        <GammaRedirect
                                            currentPath={
                                                props.location.pathname
                                            }
                                        />
                                    )}
                                />
                                <GammaDialog />
                                <GammaToast />

                                <HideFill hidden={!loading}>
                                    <GammaLoading />
                                </HideFill>

                                <Route
                                    render={props => {
                                        this.onRouteChanged(props);
                                        return null;
                                    }}
                                />

                                <HideFill hidden={loading}>
                                    <Padding>
                                        <Switch>
                                            <Route
                                                path="/home"
                                                component={Home}
                                            />
                                            <Route
                                                path="/users"
                                                component={Users}
                                            />
                                            <Route
                                                path="/groups"
                                                component={Groups}
                                            />
                                            <Route
                                                path="/create-account"
                                                component={CreateAccount}
                                            />
                                            <Route
                                                path="/login"
                                                component={Login}
                                            />
                                            <Route
                                                path="/whitelist"
                                                component={Whitelist}
                                            />
                                            <Route
                                                path="/error"
                                                component={Error}
                                            />
                                            <Route
                                                path="/posts"
                                                component={Posts}
                                            />
                                            <Route
                                                path="/websites"
                                                component={Websites}
                                            />
                                            <Route
                                                path="/activation-codes"
                                                component={ActivationCodes}
                                            />
                                            <Route
                                                path="/gdpr"
                                                component={Gdpr}
                                            />
                                            <Route
                                                path="/"
                                                render={props => (
                                                    <IfElseRendering
                                                        test={loggedIn}
                                                        ifRender={() => (
                                                            <Redirect to="/home" />
                                                        )}
                                                        elseRender={() => (
                                                            <Redirect to="/login" />
                                                        )}
                                                    />
                                                )}
                                            />
                                        </Switch>
                                    </Padding>
                                </HideFill>
                            </div>
                        )}
                    />
                )}
            />
            // <BrowserRouter>
            //     <StyledRoot>
            //         <GammaTranslations
            //             translations={appTranslations}
            //             uniquePath="App"
            //             render={text => (
            //                 <IfElseRendering
            //                     test={!loggedIn && userLoaded}
            //                     ifRender={() => (
            //                         <Route
            //                             render={props => (
            //                                 <ContainUserToAllowedPages
            //                                     currentPath={
            //                                         props.location.pathname
            //                                     }
            //                                     allowedBasePaths={[
            //                                         "/create-account",
            //                                         "/reset-password",
            //                                         "/login"
            //                                     ]}
            //                                     to="/login"
            //                                     toastTextOnRedirect={
            //                                         text.YouNeedToLogin
            //                                     }
            //                                 />
            //                             )}
            //                         />
            //                     )}
            //                 />
            //             )}
            //         />

            //         <StyledAppBar>
            //             <StyledToolbar>
            //                 <StyledMenuButton
            //                     color="inherit"
            //                     aria-label="open drawer"
            //                     onClick={this.handleDrawerToggle}
            //                 >
            //                     <MenuIcon />
            //                 </StyledMenuButton>
            //                 <HorizontalFill>
            //                     <GammaTitle text={title} white />
            // <Route
            //     render={props => (
            //         <UserInformation
            //             currentPath={
            //                 props.location.pathname
            //             }
            //         />
            //     )}
            // />
            //                 </HorizontalFill>
            //             </StyledToolbar>
            //         </StyledAppBar>
            //         <Hidden mdUp>
            //             <StyledDrawer
            //                 variant="temporary"
            //                 anchor="left"
            //                 open={mobileOpen}
            //                 onClose={this.handleDrawerToggle}
            //                 ModalProps={{
            //                     keepMounted: true // Better open performance on mobile.
            //                 }}
            //             >
            //                 {drawer}
            //             </StyledDrawer>
            //         </Hidden>
            //         <Hidden smDown implementation="css">
            //             <StyledDrawer variant="permanent" open>
            //                 {drawer}
            //             </StyledDrawer>
            //         </Hidden>
            //         <StyledMain>

            //         </StyledMain>
            //     </StyledRoot>
            // </BrowserRouter>
        );
    }
}

export default withRouter(App);
