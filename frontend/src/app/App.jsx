import {
    DigitContainUser,
    DigitDialog,
    DigitHeader,
    DigitIfElseRendering,
    DigitLayout,
    DigitNavLink,
    DigitRedirect,
    DigitToast,
    DigitTranslations
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import { Redirect, Route, Switch } from "react-router-dom";
import ActivationCodes from "../use-cases/activation-codes";
import CreateAccount from "../use-cases/create-account";
import Error from "../use-cases/error";
import Gdpr from "../use-cases/gdpr";
import Groups from "../use-cases/groups";
import Home from "../use-cases/home";
import Login from "../use-cases/login";
import Posts from "../use-cases/posts";
import Users from "../use-cases/users";
import Websites from "../use-cases/websites";
import Whitelist from "../use-cases/whitelist";
import appTranslations from "./App.translations.json";
import UserInformation from "./elements/user-information";
import GammaLoading from "./views/gamma-loading";

export class App extends Component {
    state = {
        lastPath: "/"
    };

    constructor(props) {
        super(props);
        props.userUpdateMe();
    }

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
                                <DigitTranslations
                                    translations={appTranslations}
                                    uniquePath="App"
                                    render={text => (
                                        <DigitIfElseRendering
                                            test={!loggedIn && userLoaded}
                                            ifRender={() => (
                                                <Route
                                                    render={props => (
                                                        <DigitContainUser
                                                            currentPath={
                                                                props.location
                                                                    .pathname
                                                            }
                                                            allowedBasePaths={[
                                                                "/create-account",
                                                                "/reset-password",
                                                                "/login"
                                                            ]}
                                                            to="/login"
                                                            toastTextOnRedirect={
                                                                text.YouNeedToLogin
                                                            }
                                                        />
                                                    )}
                                                />
                                            )}
                                        />
                                    )}
                                />

                                <DigitLayout.HideFill hidden={!loading}>
                                    <GammaLoading />
                                </DigitLayout.HideFill>

                                <DigitRedirect />
                                <DigitDialog />
                                <DigitToast />

                                <Route
                                    render={props => {
                                        this.onRouteChanged(props);
                                        return null;
                                    }}
                                />

                                <DigitLayout.HideFill hidden={loading}>
                                    <DigitLayout.Padding>
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
                                                    <DigitIfElseRendering
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
                                    </DigitLayout.Padding>
                                </DigitLayout.HideFill>
                            </div>
                        )}
                    />
                )}
            />
            // <BrowserRouter>
            //     <StyledRoot>
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

export default App;
