import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign,
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import translations from "./ShowUserDetails.screen.translations.json";
import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    LAST_NAME,
    NICKNAME
} from "../../../../api/users/props.users.api";

function createKeysTexts(text) {
    const output = {};

    output[CID] = text.cid;
    output[FIRST_NAME] = text.firstName;
    output[LAST_NAME] = text.lastName;
    output[NICKNAME] = text.nick;
    output[EMAIL] = text.email;
    output[ACCEPTANCE_YEAR] = text.acceptanceYear;

    return output;
}

class ShowUserDetails extends Component {
    componentDidMount() {
        const { getUser, userId, gammaLoadingFinished } = this.props;

        getUser(userId).then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const {
            user,
            dialogOpen,
            usersDelete,
            redirectTo,
            toastOpen
        } = this.props;

        console.log(user);
        return (
            <DigitIfElseRendering
                test={user != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        uniquePath="Users.Screen.ShowUserDetails"
                        render={text => (
                            <DigitLayout.Center>
                                <DigitDesign.Card
                                    minWidth="300px"
                                    maxWidth="600px"
                                >
                                    <DigitDesign.CardTitle
                                        text={
                                            user[FIRST_NAME] +
                                            " '" +
                                            user[NICKNAME] +
                                            "' " +
                                            user[LAST_NAME]
                                        }
                                    />
                                    <DigitDesign.CardBody>
                                        <DigitDisplayData
                                            data={user}
                                            keysText={createKeysTexts(text)}
                                            keysOrder={[
                                                CID,
                                                FIRST_NAME,
                                                LAST_NAME,
                                                NICKNAME,
                                                EMAIL,
                                                ACCEPTANCE_YEAR
                                            ]}
                                        />
                                    </DigitDesign.CardBody>
                                    <DigitDesign.CardButtons reverseDirection>
                                        <DigitDesign.Link
                                            to={"/users/" + user.id + "/edit"}
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
                                                    confirmButtonText:
                                                        text.DeleteUser,
                                                    cancelButtonText:
                                                        text.Cancel,
                                                    onConfirm: () => {
                                                        usersDelete(user.id)
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
                                                                redirectTo(
                                                                    "/users"
                                                                );
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
    }
}

export default ShowUserDetails;
