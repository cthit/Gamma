import React from "react";
import styled from "styled-components";
import { Fill, Center, Spacing } from "../../../../common-ui/layout";
import {
    GammaCard,
    GammaCardDisplayTitle,
    GammaCardBody,
    GammaCardButtons,
    GammaLink
} from "../../../../common-ui/design";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Text } from "../../../../common-ui/text";
import GammaDisplayData from "../../../../common/elements/gamma-display-data";
import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./ShowUserDetails.screen.translations.json";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";

import { DigitButton } from "@cthit/react-digit-components";

const ShowUserDetails = ({
    user,
    gammaDialogOpen,
    usersDelete,
    redirectTo,
    toastOpen
}) => (
    <IfElseRendering
        test={user != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Users.Screen.ShowUserDetails"
                render={text => (
                    <Center>
                        <GammaCard minWidth="300px" maxWidth="600px">
                            <GammaCardDisplayTitle
                                text={
                                    user.firstName +
                                    " '" +
                                    user.nick +
                                    "' " +
                                    user.lastName
                                }
                            />
                            <GammaCardBody>
                                <GammaDisplayData
                                    data={user}
                                    keysText={{
                                        cid: text.cid,
                                        firstName: text.firstName,
                                        lastName: text.lastName,
                                        nick: text.nick,
                                        email: text.email,
                                        acceptanceYear: text.acceptanceYear
                                    }}
                                    keysOrder={[
                                        "cid",
                                        "firstName",
                                        "lastName",
                                        "nick",
                                        "email",
                                        "acceptanceYear"
                                    ]}
                                />
                            </GammaCardBody>
                            <GammaCardButtons reverseDirection>
                                <GammaLink to={"/users/" + user.cid + "/edit"}>
                                    <DigitButton
                                        text={text.Edit}
                                        primary
                                        raised
                                    />
                                </GammaLink>
                                <Spacing />
                                <DigitButton
                                    onClick={() =>
                                        gammaDialogOpen({
                                            title:
                                                text.WouldYouLikeToDelete +
                                                " " +
                                                user.firstName +
                                                " '" +
                                                user.nick +
                                                "' " +
                                                user.lastName,
                                            confirmButtonText: text.DeleteUser,
                                            cancelButtonText: text.Cancel,
                                            onConfirm: () => {
                                                usersDelete(user.cid)
                                                    .then(response => {
                                                        toastOpen({
                                                            text:
                                                                text.DeleteSuccessfully +
                                                                " " +
                                                                user.firstName +
                                                                " '" +
                                                                user.nick +
                                                                "' " +
                                                                user.lastName
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
                                    text={text.Delete}
                                />
                            </GammaCardButtons>
                        </GammaCard>
                    </Center>
                )}
            />
        )}
    />
);

export default ShowUserDetails;
