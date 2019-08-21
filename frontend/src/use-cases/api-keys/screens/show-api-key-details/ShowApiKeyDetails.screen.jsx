import React from "react";
import {
    DigitDisplayData,
    DigitIfElseRendering,
    DigitTranslations,
    DigitLayout,
    DigitDesign,
    DigitFAB
} from "@cthit/react-digit-components";
import translations from "./ShowApiKeyDetails.screen.translations.json";
import Delete from "@material-ui/icons/Delete";
import {
    API_KEY_NAME,
    API_KEY_ID,
    DESCRIPTION,
    API_KEY_KEY
} from "../../../../api/api-keys/props.api-keys.api";
import { Add } from "@material-ui/icons";

function createKeyTexts(text) {
    const output = {};

    output[API_KEY_NAME] = text.Name;
    output[API_KEY_ID] = text.Id;
    output[API_KEY_KEY] = text.Key;
    return output;
}

class ShowApiKeyDetails extends React.Component {
    componentDidMount() {
        const { getApiKey, apiKeyId, gammaLoadingFinished } = this.props;
        getApiKey(apiKeyId).then(() => {
            gammaLoadingFinished();
        });
    }
    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const {
            apiKey,
            dialogOpen,
            toastOpen,
            deleteApiKey,
            redirectTo
        } = this.props;
        console.log(this.props);
        return (
            <DigitIfElseRendering
                test={apiKey != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <>
                                <DigitLayout.Center centerHorizontal>
                                    <DigitDesign.Card
                                        minWidth="300px"
                                        maxWidth="600px"
                                    >
                                        <DigitDesign.CardTitle
                                            text={apiKey.name}
                                        />
                                        <DigitDesign.CardBody>
                                            <DigitDisplayData
                                                data={{
                                                    name: apiKey.name,
                                                    id: apiKey.id,
                                                    key: apiKey.key
                                                }}
                                                keysText={createKeyTexts(text)}
                                                keysOrder={[
                                                    API_KEY_NAME,
                                                    API_KEY_ID,
                                                    API_KEY_KEY
                                                ]}
                                            />
                                        </DigitDesign.CardBody>
                                    </DigitDesign.Card>
                                </DigitLayout.Center>
                                <DigitLayout.DownRightPosition>
                                    <DigitLayout.Spacing />
                                    <DigitFAB
                                        onClick={() =>
                                            dialogOpen({
                                                title:
                                                    text.DeleteApiKey +
                                                    " " +
                                                    apiKey.name +
                                                    "?",
                                                confirmButtonText: text.Delete,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    deleteApiKey(apiKey.id)
                                                        .then(response => {
                                                            toastOpen({
                                                                text:
                                                                    text.DeleteSuccessfully +
                                                                    apiKey.name
                                                            });
                                                            redirectTo(
                                                                "/access_keys"
                                                            );
                                                        })
                                                        .catch(error => {
                                                            toastOpen({
                                                                text:
                                                                    text.DeleteFailed
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
    }
}

export default ShowApiKeyDetails;
