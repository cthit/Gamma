import {
    DigitHeader,
    DigitHeaderDrawer,
    DigitLayout,
    DigitLoading,
    useDigitTranslations
} from "@cthit/react-digit-components";
import React, { useCallback, useContext, useEffect, useState } from "react";
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
import Whitelist from "../use-cases/whitelist";
import translations from "../common/utils/translations/CommonTranslations";
import SuperGroups from "../use-cases/super-groups";
import Me from "../use-cases/me";
import ResetPassword from "../use-cases/reset-password";
import Drawer from "./elements/drawer";
import Members from "../use-cases/members";
import { getRequest } from "../api/utils/api";
import GammaUserContext from "../common/context/GammaUser.context";
import FiveZeroZero from "./elements/five-zero-zero";
import { getBackendUrl } from "../common/utils/configs/envVariablesLoader";

export const App = () => {
    const [user, setUser] = useContext(GammaUserContext);
    const [, , , setCommonTranslations] = useDigitTranslations(translations);
    const [[loading, error], setStatus] = useState([true, false]);
    const { pathname } = useLocation();

    const title = "Gamma";

    const getMe = useCallback(
        () =>
            getRequest("/users/me")
                .then(response => {
                    setUser(response.data);
                    setStatus([false, false]);
                })
                .catch(error => {
                    if (error.response.status === 401) {
                        window.location.href = getBackendUrl() + "/login";
                    } else {
                        console.log(error);
                        setStatus([false, true]);
                    }
                }),
        [setUser]
    );

    useEffect(() => {
        setCommonTranslations(translations);
        // translations can't change in run time since it's a json file
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (
            loading &&
            !user &&
            !pathname.startsWith("/create-account") &&
            (!pathname.startsWith("/reset-password") ||
                pathname.startsWith("/reset-password/admin"))
        ) {
            getMe();
        } else if (
            pathname.startsWith("/create-account") ||
            pathname.startsWith("/reset-password")
        ) {
            setStatus([false, false]);
        }
    }, [loading, error, pathname, getMe, user]);

    const main = (
        <>
            {loading && (
                <DigitLoading loading alignSelf={"center"} margin={"auto"} />
            )}
            {error && <FiveZeroZero getMe={getMe} />}
            {!loading && !error && (
                <div
                    style={{
                        width: "100%",
                        minHeight: "calc(100vh - 208px)"
                    }}
                >
                    <DigitLayout.Hide hidden={loading}>
                        <Switch>
                            <Route path="/clients" component={Clients} />
                            <Route path="/users" component={Users} />
                            <Route path="/groups" component={Groups} />
                            <Route
                                path="/create-account"
                                component={CreateAccount}
                            />
                            <Route path="/whitelist" component={Whitelist} />
                            <Route path="/posts" component={Posts} />
                            <Route
                                path="/activation-codes"
                                component={ActivationCodes}
                            />
                            <Route path="/gdpr" component={Gdpr} />
                            <Route path="/clients" component={Clients} />
                            <Route path="/access-keys" component={ApiKeys} />
                            <Route
                                path="/super-groups"
                                component={SuperGroups}
                            />
                            <Route path={"/me"} component={Me} />
                            <Route path="/members" component={Members} />
                            <Route
                                path="/reset-password"
                                component={ResetPassword}
                            />
                            <Route path="/" exact component={Home} />
                            <Route component={FourOFour} />
                        </Switch>
                    </DigitLayout.Hide>
                </div>
            )}
        </>
    );

    const headerOptions = {
        title,
        headerHeight: "200px",
        backgroundImage: "url(/enbarsskar.jpg)",
        renderMain: () => main
    };

    if (user == null) {
        return <DigitHeader {...headerOptions} />;
    }

    return (
        <DigitHeaderDrawer
            {...headerOptions}
            renderDrawer={closeDrawer =>
                user == null ? null : <Drawer closeDrawer={closeDrawer} />
            }
        />
    );
};

export default App;
