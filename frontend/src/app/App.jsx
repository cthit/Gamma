import {
    DigitContainUser,
    DigitDialog,
    DigitHeader,
    DigitIfElseRendering,
    DigitLayout,
    DigitNavLink,
    DigitRedirect,
    DigitToast,
    DigitTranslations,
    DigitRedirectExternal
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import { Route, Switch } from "react-router-dom";
import ActivationCodes from "../use-cases/activation-codes";
import CreateAccount from "../use-cases/create-account";
import FourOFour from "../use-cases/four-o-four";
import Gdpr from "../use-cases/gdpr";
import Groups from "../use-cases/groups";
import Home from "../use-cases/home";
import Posts from "../use-cases/posts";
import Users from "../use-cases/users";
import Clients from "../use-cases/clients";
import Websites from "../use-cases/websites";
import Whitelist from "../use-cases/whitelist";
import appTranslations from "./App.translations.json";
import UserInformation from "./elements/user-information";
import GammaLoading from "./views/gamma-loading";
import commonTranslations from "../common/utils/translations/CommonTranslations.json";
import SuperGroups from "../use-cases/super-groups";
import GammaIntegration from "./views/gamma-integration/GammaIntegration.view.container";

export class App extends Component {
    state = {
        lastPath: "/"
    };

    constructor(props) {
        super(props);
        props.setCommonTranslations(commonTranslations);
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
        const baseUrl = "http://localhost:8081/api/oauth/authorize";
        const responseType = "response_type=code";
        const clientId =
            "client_id=7hAdUEtMo4MgFnA7ZoZ41ohTe1NNRoJmjL67Gf0NIrrBnauyhc";
        const redirectUri = "redirect_uri=http://localhost:3000/login";

        const loginRedirect =
            baseUrl + "?" + responseType + "&" + clientId + "&" + redirectUri;

        const title = "Gamma - IT-konto";

        const drawer = closeDrawer => (
            <DigitLayout.Column padding="0">
                <DigitNavLink onClick={closeDrawer} text="Home" link="/" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Create account"
                    link="/create-account"
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
                    onClose={closeDrawer}
                    text="Super groups"
                    link="/super-groups"
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
                <DigitNavLink
                    onClick={closeDrawer}
                    text={"Clients"}
                    link={"/clients"}
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

        // alert(
        //     "Loading:" +
        //         loading +
        //         ";" +
        //         "Logged in: " +
        //         loggedIn +
        //         "; userLoaded: " +
        //         userLoaded +
        //         " COMBINED: " +
        //         (!loggedIn && userLoaded && !loading)
        // );

        return (
            <DigitTranslations
                uniquePath="App"
                translations={appTranslations}
                render={text => (
                    <DigitHeader
                        headerHeight={"200px"}
                        cssImageString={"url(/matterhorn.jpg)"}
                        title={title}
                        renderDrawer={loggedIn ? drawer : () => null}
                        renderHeader={header}
                        renderMain={() => (
                            <DigitLayout.Fill>
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
                                                                "/reset-password"
                                                            ]}
                                                            to={loginRedirect}
                                                            externalRedirect
                                                        />
                                                    )}
                                                />
                                            )}
                                        />
                                    )}
                                />

                                <DigitLayout.HideFill hidden={!loading}>
                                    <DigitLayout.Center>
                                        <GammaLoading />
                                    </DigitLayout.Center>
                                </DigitLayout.HideFill>

                                <DigitRedirect window={window} />
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
                                                path="/login"
                                                render={() => (
                                                    <GammaIntegration />
                                                )}
                                            />
                                            <Route
                                                path="/clients"
                                                component={Clients}
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
                                                path="/whitelist"
                                                component={Whitelist}
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
                                                path="/clients"
                                                component={Clients}
                                            />
                                            <Route
                                                path="/super-groups"
                                                component={SuperGroups}
                                            />
                                            <Route
                                                path="/"
                                                render={() => (
                                                    <DigitIfElseRendering
                                                        test={
                                                            !loading &&
                                                            !loggedIn &&
                                                            userLoaded
                                                        }
                                                        ifRender={() => (
                                                            <DigitRedirectExternal
                                                                window={window}
                                                                to={
                                                                    loginRedirect
                                                                }
                                                            />
                                                        )}
                                                        elseRender={() => (
                                                            <Home />
                                                        )}
                                                    />
                                                )}
                                            />
                                            <Route component={FourOFour} />
                                        </Switch>
                                    </DigitLayout.Padding>
                                </DigitLayout.HideFill>
                            </DigitLayout.Fill>
                        )}
                    />
                )}
            />
        );
    }
}

export default App;
