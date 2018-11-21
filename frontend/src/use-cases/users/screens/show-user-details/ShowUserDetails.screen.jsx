import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign,
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowUserDetails.screen.translations.json";

const ShowUserDetails = ({
    user,
    dialogOpen,
    usersDelete,
    redirectTo,
    toastOpen
}) => (
    <DigitIfElseRendering
        test={user != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Users.Screen.ShowUserDetails"
                render={text => (
                    <DigitLayout.Center>
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
                                <DigitLayout.Spacing />
                                <DigitButton
                                    onClick={() =>
                                        dialogOpen({
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
                    </DigitLayout.Center>
                )}
            />
        )}
    />
);

export default ShowUserDetails;
