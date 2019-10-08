import React, { useEffect } from "react";
import UserForm from "../../../../common/elements/user-form";
import translations from "./CreateNewUser.screen.translations";
import { DigitLayout, DigitTranslations } from "@cthit/react-digit-components";
import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    LAST_NAME,
    NICK,
    PASSWORD,
    USER_AGREEMENT,
    WEBSITES
} from "../../../../api/users/props.users.api";
import useIsAdmin from "../../../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../../../common/views/insufficient-access";

function generateInitialValues() {
    const output = {};

    output[FIRST_NAME] = "";
    output[LAST_NAME] = "";
    output[CID] = "";
    output[NICK] = "";
    output[EMAIL] = "";
    output[PASSWORD] = "";
    output[WEBSITES] = [];

    return output;
}

const CreateNewUser = ({
    websites,
    addUser,
    websitesLoad,
    gammaLoadingFinished
}) => {
    const admin = useIsAdmin();

    useEffect(() => {
        if (admin) {
            websitesLoad().then(() => {
                gammaLoadingFinished();
            });
        }
    });

    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitTranslations
            translations={translations}
            render={text => (
                <DigitLayout.Center>
                    <UserForm
                        includeCidAndPassword
                        initialValues={generateInitialValues()}
                        availableWebsites={websites}
                        titleText={text.CreateUser}
                        submitText={text.Create}
                        onSubmit={(values, actions) => {
                            const output = { ...values };
                            output[USER_AGREEMENT] = true;
                            delete output[WEBSITES];
                            output[ACCEPTANCE_YEAR] = parseInt(
                                values[ACCEPTANCE_YEAR]
                            );
                            addUser(output);
                            // actions.resetForm();
                        }}
                    />
                </DigitLayout.Center>
            )}
        />
    );
};

export default CreateNewUser;
