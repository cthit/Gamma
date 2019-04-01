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
import {
    ID,
    NAME,
    EMAIL,
    DESCRIPTION,
    FUNCTION
} from "../../../../api/groups/props.groups.api";
import DisplayUsersTable from "../../../../common/elements/display-users-table";

const DESCRIPTION_SV = "description_sv";
const DESCRIPTION_EN = "description_en";
const FUNCTION_SV = "function_sv";
const FUNCTION_EN = "function_en";

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

    generateKeysText = text => {
        const output = {};

        output[ID] = text.Id;
        output[NAME] = text.Name;
        output[DESCRIPTION_SV] = text.DescriptionSv;
        output[DESCRIPTION_EN] = text.DescriptionEn;
        output[EMAIL] = text.Email;
        output[FUNCTION_SV] = text.FunctionSv;
        output[FUNCTION_EN] = text.FunctionEn;

        return output;
    };

    modifyData = group => {
        const newGroup = {};

        newGroup[ID] = group[ID];
        newGroup[NAME] = group[NAME];
        newGroup[DESCRIPTION_SV] = group[DESCRIPTION]["sv"];
        newGroup[DESCRIPTION_EN] = group[DESCRIPTION]["en"];
        newGroup[EMAIL] = group[EMAIL];
        newGroup[FUNCTION_SV] = group[FUNCTION]["sv"];
        newGroup[FUNCTION_EN] = group[FUNCTION]["en"];

        return newGroup;
    };

    render() {
        const {
            group,
            groupsDelete,
            dialogOpen,
            toastOpen,
            redirectTo
        } = this.props;

        return (
            <DigitIfElseRendering
                test={group != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Column centerHorizontal>
                                <DigitDesign.Card
                                    minWidth="300px"
                                    maxWidth="600px"
                                >
                                    <DigitDesign.CardBody>
                                        <DigitDisplayData
                                            data={this.modifyData(group)}
                                            keysText={this.generateKeysText(
                                                text
                                            )}
                                            keysOrder={[
                                                ID,
                                                NAME,
                                                DESCRIPTION_SV,
                                                DESCRIPTION_EN,
                                                EMAIL,
                                                FUNCTION_SV,
                                                FUNCTION_EN
                                            ]}
                                        />
                                    </DigitDesign.CardBody>
                                    <DigitDesign.CardButtons reverseDirection>
                                        <DigitDesign.Link
                                            to={"/groups/" + group.id + "/edit"}
                                        >
                                            <DigitButton
                                                primary
                                                raised
                                                text="Redigera"
                                            />
                                        </DigitDesign.Link>
                                        <DigitDesign.Link
                                            to={
                                                "/groups/" +
                                                group.id +
                                                "/members"
                                            }
                                        >
                                            <DigitButton
                                                outlined
                                                primary
                                                text="Redigera medlemmar"
                                            />
                                        </DigitDesign.Link>
                                        <DigitButton
                                            text="Radera"
                                            onClick={() => {
                                                dialogOpen({
                                                    title: "Radera?",
                                                    confirmButtonText: "Radera",
                                                    cancelButtonText:
                                                        text.Cancel,
                                                    onConfirm: () => {
                                                        groupsDelete(group.id)
                                                            .then(() => {
                                                                toastOpen({
                                                                    text:
                                                                        "Du har raderat"
                                                                });

                                                                redirectTo(
                                                                    "/groups"
                                                                );
                                                            })
                                                            .catch(error => {
                                                                toastOpen({
                                                                    text:
                                                                        text.SomethingWentWrong
                                                                });
                                                            });
                                                    }
                                                });
                                            }}
                                        />
                                    </DigitDesign.CardButtons>
                                </DigitDesign.Card>
                                <DisplayUsersTable
                                    users={group.groupMembers}
                                    noUsersText={text.NoGroupMembers}
                                />
                            </DigitLayout.Column>
                        )}
                    />
                )}
            />
        );
    }
}

export default ShowGroupDetails;
