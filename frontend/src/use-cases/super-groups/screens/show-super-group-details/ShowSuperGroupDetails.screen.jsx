import React from "react";
import {
    DigitButton,
    DigitDisplayData,
    DigitIfElseRendering,
    DigitTranslations,
    DigitLayout,
    DigitDesign
} from "@cthit/react-digit-components";
import translations from "./ShowSuperGroupDetails.screen.translations";
import {
    ID,
    NAME,
    PRETTY_NAME,
    TYPE
} from "../../../../api/super-groups/props.super-groups.api";
import ShowSubGroups from "./elements/show-sub-groups";

class ShowSuperGroupDetails extends React.Component {
    constructor(props) {
        super(props);

        const {
            getSuperGroup,
            getSuperGroupSubGroups,
            gammaLoadingFinished,
            superGroupId
        } = this.props;

        Promise.all([
            getSuperGroup(superGroupId),
            getSuperGroupSubGroups(superGroupId)
        ]).then(() => {
            gammaLoadingFinished();
        });
    }

    createKeyTexts = text => {
        const output = {};

        output[ID] = text.Id;
        output[NAME] = text.Name;
        output[PRETTY_NAME] = text.PrettyName;
        output[TYPE] = text.Type;

        return output;
    };

    render() {
        const {
            superGroup,
            deleteSuperGroup,
            dialogOpen,
            toastOpen,
            redirectTo
        } = this.props;

        return (
            <DigitIfElseRendering
                test={
                    superGroup != null &&
                    superGroup.data != null &&
                    superGroup.subGroups != null
                }
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Column centerHorizontal>
                                <DigitDesign.Card
                                    minWidth="300px"
                                    maxWidth="600px"
                                >
                                    <DigitDesign.CardTitle
                                        text={superGroup.data[PRETTY_NAME]}
                                    />
                                    <DigitDesign.CardBody>
                                        <DigitDisplayData
                                            data={superGroup.data}
                                            keysText={this.createKeyTexts(text)}
                                            keysOrder={[
                                                ID,
                                                NAME,
                                                PRETTY_NAME,
                                                TYPE
                                            ]}
                                        />
                                    </DigitDesign.CardBody>
                                    <DigitDesign.CardButtons reverseDirection>
                                        <DigitDesign.Link
                                            to={
                                                "/super-groups/" +
                                                superGroup.data.id +
                                                "/edit"
                                            }
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
                                                        superGroup.name,
                                                    confirmButtonText:
                                                        text.DeleteSuperGroup,
                                                    cancelButtonText:
                                                        text.Cancel,
                                                    onConfirm: () => {
                                                        deleteSuperGroup(
                                                            superGroup.data.id
                                                        )
                                                            .then(response => {
                                                                toastOpen({
                                                                    text:
                                                                        text.DeleteSuccessfully +
                                                                        " " +
                                                                        superGroup.name
                                                                });
                                                                redirectTo(
                                                                    "/super-groups"
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
                                <ShowSubGroups
                                    title={text.SubGroups}
                                    subGroups={superGroup.subGroups}
                                />
                            </DigitLayout.Column>
                        )}
                    />
                )}
            />
        );
    }
}

export default ShowSuperGroupDetails;
