import React, { useEffect } from "react";
import translations from "./ChangeUserPassword.screen.translations";
import ChangeUserPasswordForm from "../../../../common/elements/change-user-password";
import {
    DigitIfElseRendering,
    DigitTranslations
} from "@cthit/react-digit-components";
import useIsAdmin from "../../../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../../../common/views/insufficient-access";

const ChangeUserPassword = ({
    user,
    changePassword,
    redirectTo,
    toastOpen,
    getUser,
    userId,
    deltaLoadingFinished
}) => {
    const admin = useIsAdmin();

    useEffect(() => {
        if (!admin) {
            getUser(userId).then(() => {
                deltaLoadingFinished();
            });
        }
    });

    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitIfElseRendering
            test={user != null}
            ifRender={() => (
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <ChangeUserPasswordForm
                            renderOldPassword={false}
                            user={user.cid}
                            onSubmit={(values, actions) => {
                                changePassword(user.id, values).then(
                                    response => {
                                        toastOpen({
                                            text:
                                                text.ChangePasswordSuccessfully +
                                                ' "' +
                                                user.cid +
                                                '"',
                                            duration: 5000
                                        });
                                        redirectTo("/users/" + user.id);
                                    }
                                );
                            }}
                        />
                    )}
                />
            )}
        />
    );
};

export default ChangeUserPassword;
