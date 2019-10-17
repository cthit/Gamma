import React, { useEffect } from "react";

import { DigitLayout } from "@cthit/react-digit-components";

import UserOptions from "./elements/user-options";
import AdminOptions from "./elements/admin-options";
import WelcomeUser from "./elements/welcome-user";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";

const Home = ({ user, deltaLoadingFinished }) => {
    useEffect(() => {
        deltaLoadingFinished();
    });

    const admin = useIsAdmin();

    return (
        <DigitLayout.Center>
            <DigitLayout.Padding>
                <DigitLayout.Column>
                    <WelcomeUser user={user} />
                    <DigitLayout.Spacing />
                    <UserOptions
                        hasGroups={
                            user.groups != null && user.groups.length > 0
                        }
                    />
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
