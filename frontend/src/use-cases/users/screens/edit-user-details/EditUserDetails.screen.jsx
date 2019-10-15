import {
    DigitIfElseRendering,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";
import React, { useEffect } from "react";
import UserForm from "../../../../common/elements/user-form/UserForm.view";
import translations from "./EditUserDetails.screen.translations.json";
import useIsAdmin from "../../../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../../../common/views/insufficient-access";

const EditUserDetails = ({
    user,
    usersChange,
    websites,
    toastOpen,
    redirectTo,
    getUser,
    websitesLoad,
    gammaLoadingFinished,
    gammaLoadingStart,
    userId
}) => {
    const admin = useIsAdmin();

    useEffect(() => {
        if (admin) {
            Promise.all([getUser(userId), websitesLoad()]).then(() => {
                gammaLoadingFinished();
            });
        }
    });

    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitIfElseRendering
            test={user != null && websites != null}
            ifRender={() => (
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <DigitLayout.Center>
                            <UserForm
                                availableWebsites={websites}
                                titleText={text.EditUser}
                                submitText={text.SaveUser}
                                initialValues={{
                                    ...user,
                                    websites:
                                        user.websites == null
                                            ? []
                                            : user.websites,
                                    acceptanceYear: user.acceptanceYear + ""
                                }}
                                onSubmit={(values, actions) => {
                                    usersChange(values, user.id).then(
                                        response => {
                                            toastOpen({
                                                text:
                                                    text.UserUpdated + user.cid
                                            });
                                            redirectTo("/users/" + user.id);
                                        }
                                    );
                                }}
                            />
                        </DigitLayout.Center>
                    )}
                />
            )}
        />
    );
};

export default EditUserDetails;
