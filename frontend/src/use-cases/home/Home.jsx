import React, { useEffect } from "react";

import { DigitLayout, useGammaUser } from "@cthit/react-digit-components";

import UserOptions from "./elements/user-options";
import AdminOptions from "./elements/admin-options";
import WelcomeUser from "./elements/welcome-user";
import useIsAdmin from "../../common/hooks/use-is/use-is-admin";

const Home = () => {
    const admin = useIsAdmin();
    const user = useGammaUser();

    if (user == null) {
        return null;
    }

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
