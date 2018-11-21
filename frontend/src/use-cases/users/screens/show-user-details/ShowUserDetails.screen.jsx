import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign
} from "@cthit/react-digit-components";
import React from "react";
import { Center, Spacing } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./ShowUserDetails.screen.translations.json";

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
                        <DigitDesign.Card minWidth="300px" maxWidth="600px">
                            <DigitDesign.CardDisplayTitle
                                text={
                                    user.firstName +
                                    " '" +
                                    user.nick +
                                    "' " +
                                    user.lastName
                                }
                            />
                            <DigitDesign.CardBody>
                                <DigitDisplayData
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
                            </DigitDesign.CardBody>
                            <DigitDesign.CardButtons reverseDirection>
                                <DigitDesign.Link
                                    to={"/users/" + user.cid + "/edit"}
                                >
                                    <DigitButton
                                        text={text.Edit}
                                        primary
                                        raised
                                    />
                                </DigitDesign.Link>
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
                            </DigitDesign.CardButtons>
                        </DigitDesign.Card>
                    </Center>
                )}
            />
        )}
    />
);

export default ShowUserDetails;
