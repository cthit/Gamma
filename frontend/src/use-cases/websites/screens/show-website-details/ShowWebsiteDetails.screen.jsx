import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign,
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import translations from "./ShowWebsiteDetails.screen.translations.json";
import {
    NAME,
    PRETTY_NAME,
    WEBSITE_ID
} from "../../../../api/websites/props.websites.api";

function generateKeyTexts(text) {
    const keyTexts = {};

    keyTexts[WEBSITE_ID] = text.Id;
    keyTexts[NAME] = text.Name;
    keyTexts[PRETTY_NAME] = text.PrettyName;

    return keyTexts;
}

class ShowWebsiteDetails extends Component {
    componentDidMount() {
        const { websiteId, getWebsite, gammaLoadingFinished } = this.props;

        getWebsite(websiteId).then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const {
            website,
            dialogOpen,
            toastOpen,
            redirectTo,
            websitesDelete
        } = this.props;
        return (
            <DigitIfElseRendering
                test={website != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Fill>
                                <DigitLayout.Center>
                                    <DigitDesign.Card
                                        minWidth="300px"
                                        maxWidth="600px"
                                    >
                                        <DigitDesign.CardBody>
                                            <DigitDisplayData
                                                data={website}
                                                keysText={generateKeyTexts(
                                                    text
                                                )}
                                                keysOrder={[
                                                    WEBSITE_ID,
                                                    NAME,
                                                    PRETTY_NAME
                                                ]}
                                            />
                                        </DigitDesign.CardBody>
                                        <DigitDesign.CardButtons
                                            reverseDirection
                                        >
                                            <DigitDesign.Link
                                                to={
                                                    "/websites/" +
                                                    website.id +
                                                    "/edit"
                                                }
                                            >
                                                <DigitButton
                                                    text={text.EditWebsite}
                                                    primary
                                                    raised
                                                />
                                            </DigitDesign.Link>
                                            <DigitLayout.Spacing />
                                            <DigitButton
                                                onClick={() => {
                                                    dialogOpen({
                                                        title:
                                                            text.WouldYouLikeToDelete +
                                                            " " +
                                                            website.prettyName,
                                                        confirmButtonText:
                                                            text.DeleteWebsite,
                                                        cancelButtonText:
                                                            text.Cancel,
                                                        onConfirm: () => {
                                                            websitesDelete(
                                                                website.id
                                                            )
                                                                .then(() => {
                                                                    toastOpen({
                                                                        text:
                                                                            website.prettyName +
                                                                            " " +
                                                                            text.Deleted
                                                                    });
                                                                    redirectTo(
                                                                        "/websites"
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
                                                text={text.DeleteWebsite}
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

export default ShowWebsiteDetails;
