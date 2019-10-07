import React, { useEffect } from "react";

import { DigitLayout } from "@cthit/react-digit-components";

import UserOptions from "./elements/user-options";
import AdminOptions from "./elements/admin-options";
import WelcomeUser from "./elements/welcome-user";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";

const Home = ({ user, gammaLoadingFinished }) => {
    useEffect(() => {
        gammaLoadingFinished();
    });

    const admin = useIsAdmin();

    console.log("HEJ");
    console.log(admin);

    return (
        <DigitLayout.Center>
            <DigitLayout.Padding>
                <DigitLayout.Column>
                    <WelcomeUser user={user} />
                    <DigitLayout.Spacing />
                    <UserOptions />
                    {admin && (
                        <>
                            <DigitLayout.Spacing />
                            <AdminOptions />
                        </>
                    )}
                </DigitLayout.Column>
            </DigitLayout.Padding>
        </DigitLayout.Center>
    );
};

export default Home;
