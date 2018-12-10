import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign,
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import translations from "./ShowGroupDetails.screen.translations.json";

class ShowGroupDetails extends Component {
    componentDidMount() {
        const { groupId, getGroup, gammaLoadingFinished } = this.props;

        getGroup(groupId).then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const {
            group,
            groupsDelete,
            dialogOpen,
            toastOpen,
            redirectTo
        } = this.props;

        console.log(group);

        return (
            <DigitIfElseRendering
                test={group != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        uniquePath="Groups.Screen.ShowGroupDetails"
                        render={text => (
                            <DigitLayout.Fill>
                                <DigitLayout.Center>
                                    <DigitDesign.Card
                                        minWidth="300px"
                                        maxWidth="600px"
                                    >
                                        <DigitDesign.CardBody>
                                            <DigitDisplayData
                                                data={{
                                                    id: group.id,
                                                    name: group.name,
                                                    description_sv:
                                                        group.description.sv,
                                                    description_en:
                                                        group.description.en,
                                                    email: group.email,
                                                    func_sv:
                                                        group.func.sv,
                                                    func_en:
                                                        group.func.en,
                                                    type: _getTypeText(
                                                        group.type,
                                                        text
                                                    )
                                                }}
                                                keysText={{
                                                    id: text.Id,
                                                    name: text.Name,
                                                    description_sv:
                                                        text.DescriptionSv,
                                                    description_en:
                                                        text.DescriptionEn,
                                                    email: text.Email,
                                                    func_sv: text.FunctionSv,
                                                    func_en: text.FunctionEn,
                                                    type: _getTypeText(
                                                        group.type,
                                                        text
                                                    )
                                                }}
                                                keysOrder={[
                                                    "id",
                                                    "name",
                                                    "description_sv",
                                                    "description_en",
                                                    "email",
                                                    "func_sv",
                                                    "func_en",
                                                    "type"
                                                ]}
                                            />
                                        </DigitDesign.CardBody>
                                        <DigitDesign.CardButtons
                                            reverseDirection
                                        >
                                            <DigitDesign.Link
                                                to={
                                                    "/groups/" +
                                                    group.id +
                                                    "/edit"
                                                }
                                            >
                                                <DigitButton
                                                    primary
                                                    raised
                                                    text="Redigera"
                                                />
                                            </DigitDesign.Link>
                                            <DigitButton
                                                text="Radera"
                                                onClick={() => {
                                                    dialogOpen({
                                                        title: "Radera?",
                                                        confirmButtonText:
                                                            "Radera",
                                                        cancelButtonText:
                                                            text.Cancel,
                                                        onConfirm: () => {
                                                            groupsDelete(
                                                                group.id
                                                            )
                                                                .then(() => {
                                                                    toastOpen({
                                                                        text:
                                                                            "Du har raderat"
                                                                    });

                                                                    redirectTo(
                                                                        "/groups"
                                                                    );
                                                                })
                                                                .catch(
                                                                    error => {
                                                                        toastOpen(
                                                                            {
                                                                                text:
                                                                                    text.SomethingWentWrong
                                                                            }
                                                                        );
                                                                    }
                                                                );
                                                        }
                                                    });
                                                }}
                                            />
                                        </DigitDesign.CardButtons>
                                    </DigitDesign.Card>
                                </DigitLayout.Center>
                            </DigitLayout.Fill>
                        )}
                    />
                )}
            />
        );
    }
}

function _getTypeText(type, text) {
    switch (type) {
        case "SOCIETY":
            return text.society;
        case "COMMITTEE":
            return text.Committee;
        case "BOARD":
            return text.Board;
        default:
            return "Unknown";
    }
}

export default ShowGroupDetails;
