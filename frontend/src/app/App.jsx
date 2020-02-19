import {
    DigitDialog,
    DigitHeader,
    DigitLayout,
    DigitToast,
    useDigitTranslations,
    useGamma,
    useGammaSignIn,
    useGammaUser,
    useGammaStatus
} from "@cthit/react-digit-components";
import React, { useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import ActivationCodes from "../use-cases/activation-codes";
import CreateAccount from "../use-cases/create-account";
import FourOFour from "../use-cases/four-o-four";
import Gdpr from "../use-cases/gdpr";
import Groups from "../use-cases/groups";
import Home from "../use-cases/home";
import Posts from "../use-cases/posts";
import Users from "../use-cases/users";
import Clients from "../use-cases/clients";
import ApiKeys from "../use-cases/api-keys";
import Websites from "../use-cases/websites";
import Whitelist from "../use-cases/whitelist";
import translations from "./App.translations.json";
import commonTranslations from "../common/utils/translations/CommonTranslations";
import UserInformation from "./elements/user-information";
import SuperGroups from "../use-cases/super-groups";
import Me from "../use-cases/me";
import ResetPassword from "../use-cases/reset-password";
import Drawer from "./elements/drawer";
import Members from "../use-cases/members";
import {
    getBackendUrl,
    getFrontendUrl
} from "../common/utils/configs/envVariablesLoader";

export const App = ({}) => {
    const [, , , setCommonTranslations] = useDigitTranslations(translations);

    const title = "Gamma";

    useGamma({
        name: "gamma",
        id: "7hAdUEtMo4MgFnA7ZoZ41ohTe1NNRoJmjL67Gf0NIrrBnauyhc",
        secret: "secret",
        redirect: getFrontendUrl() + "/login",
        gammaPath: getBackendUrl(),
        forceSignedIn: false,
        signOutFromGamma: true,
        refreshOnFocus: true,
        toast: true
    });
    const user = useGammaUser();
    const [loading, error] = useGammaStatus();
    const { pathname } = useLocation();
    const signIn = useGammaSignIn();

    useEffect(() => {
        setCommonTranslations(commonTranslations);
    }, []);

    useEffect(() => {
        if (!loading && !user && !pathname.startsWith("/create-account")) {
            signIn();
        }
    }, [loading, pathname, user]);

    if (loading) {
        return null;
    }

    return (
        <DigitHeader
            headerHeight={"200px"}
            cssImageString={"url(/enbarsskar.jpg)"}
            title={title}
            renderDrawer={closeDrawer =>
                user == null ? null : <Drawer closeDrawer={closeDrawer} />
            }
            renderHeader={() => <UserInformation />}
            renderMain={() => (
                <DigitLayout.Fill>
                    {!error && (
                        <>
                            <DigitDialog />
                            <DigitToast />

                            <div
                                style={{
                                    padding: "8px",
                                    display: "flex",
                                    flex: "1"
                                }}
                            >
                                <DigitLayout.HideFill hidden={loading}>
                                    <Switch>
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
                                        <Route path="/gdpr" component={Gdpr} />
                                        <Route
                                            path="/clients"
                                            component={Clients}
                                        />
                                        <Route
                                            path="/access-keys"
                                            component={ApiKeys}
                                        />
                                        <Route
                                            path="/super-groups"
                                            component={SuperGroups}
                                        />
                                        <Route path={"/me"} component={Me} />
                                        <Route
                                            path="/members"
                                            component={Members}
                                        />
                                        <Route
                                            path="/reset-password"
                                            component={ResetPassword}
                                        />
                                        <Route
                                            path="/"
                                            exact
                                            component={Home}
                                        />
                                        <Route component={FourOFour} />
                                    </Switch>
                                </DigitLayout.HideFill>
                            </div>
                        </>
                    )}
                </DigitLayout.Fill>
            )}
        />
    );
};

export default App;
