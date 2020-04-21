import React from "react";

import { DigitLayout } from "@cthit/react-digit-components";

import UserOptions from "./elements/user-options";
import AdminOptions from "./elements/admin-options";
import WelcomeUser from "./elements/welcome-user";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import useGammaUser from "../../common/hooks/use-gamma-user/useGammaUser";

const Home = () => {
    const admin = useGammaIsAdmin();
    const user = useGammaUser();

    if (user == null) {
        return null;
    }

    return (
        <DigitLayout.Center>
            <DigitLayout.Column>
                <WelcomeUser user={user} />
                <DigitLayout.Spacing />
                <UserOptions
                    hasGroups={
                        user.groups != null &&
                        user.groups.length > 0
                    }
                />
                {admin && (
                    <>
                        <DigitLayout.Spacing />
                        <AdminOptions />
                    </>
                )}
            </DigitLayout.Column>
        </DigitLayout.Center>
    );
};

export default Home;
