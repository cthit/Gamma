import {
    DigitFAB,
    DigitIfElseRendering,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";
import React, { useEffect } from "react";
import translations from "./ShowUserDetails.screen.translations.json";
import DisplayUserDetails from "../../../../common/elements/display-user-details/DisplayUserDetails.element";
import Delete from "@material-ui/icons/Delete";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../api/users/props.users.api";
import useIsAdmin from "../../../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../../../common/views/insufficient-access";

const ShowUserDetails = ({
    user,
    dialogOpen,
    usersDelete,
    redirectTo,
    toastOpen,
    signedInUser,
    getUser,
    userId,
    gammaLoadingFinished
}) => {
    const admin = useIsAdmin();

    useEffect(() => {
        if (admin) {
            getUser(userId).then(() => {
                gammaLoadingFinished();
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
                        <>
                            <DigitLayout.Center>
                                <DisplayUserDetails user={user} isMe={false} />
                            </DigitLayout.Center>
                            <DigitLayout.DownRightPosition>
                                <DigitLayout.Spacing />
                                <DigitFAB
                                    onClick={() =>
                                        dialogOpen({
                                            title:
                                                user.cid === signedInUser.cid
                                                    ? text.DeleteAdmin
                                                    : text.WouldYouLikeToDelete +
                                                      " " +
                                                      user.firstName +
                                                      " '" +
                                                      user.nick +
                                                      "' " +
                                                      user.lastName,
                                            confirmButtonText: text.DeleteUser,
                                            cancelButtonText: text.Cancel,
                                            onConfirm: () => {
                                                usersDelete(user.id)
                                                    .then(response => {
                                                        toastOpen({
                                                            text:
                                                                text.DeleteSuccessfully +
                                                                " " +
                                                                user[
                                                                    FIRST_NAME
                                                                ] +
                                                                " '" +
                                                                user[NICK] +
                                                                "' " +
                                                                user[LAST_NAME]
                                                        });
                                                        redirectTo("/users");
                                                    })
                                                    .catch(error => {
                                                        toastOpen({
                                                            text:
                                                                text.SomethingWentWrong
                                                        });
                                                    });
                                            }
                                        })
                                    }
                                    icon={Delete}
                                    text={text.Delete}
                                    secondary
                                />
                            </DigitLayout.DownRightPosition>
                        </>
                    )}
                />
            )}
        />
    );
};

export default ShowUserDetails;
