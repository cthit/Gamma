import React from "react";
import {
    DigitDisplayData,
    DigitIfElseRendering,
    DigitTranslations,
    DigitLayout,
    DigitDesign,
    DigitFAB
} from "@cthit/react-digit-components";
import translations from "./ShowClientDetails.screen.translations.json";
import Delete from "@material-ui/icons/Delete";
import {
    CLIENT_NAME,
    CLIENT_REDIRECT,
    CLIENT_ID
} from "../../../../api/clients/props.clients.api";

const DESCRIPTION_SV = "descriptionSv";
const DESCRIPTION_EN = "descriptionEn";

function createKeyTexts(text) {
    const output = {};

    output[CLIENT_NAME] = text.Name;
    output[DESCRIPTION_EN] = text.DescriptionEn;
    output[DESCRIPTION_SV] = text.DescriptionSv;
    output[CLIENT_REDIRECT] = text.Redirect;
    output[CLIENT_ID] = text.ClientId;
    return output;
}
class ShowClientDetails extends React.Component {
    componentDidMount() {
        const { getClient, clientId, gammaLoadingFinished } = this.props;

        getClient(clientId).then(() => {
            gammaLoadingFinished();
        });
    }
    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const {
            client,
            dialogOpen,
            toastOpen,
            deleteClient,
            redirectTo
        } = this.props;
        return (
            <DigitIfElseRendering
                test={
                    // Lägg till conditions för att kolla så värden inte är null.
                    client != null
                }
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
                                            text={client.name}
                                        />
                                        <DigitDesign.CardBody>
                                            <DigitDisplayData
                                                data={{
                                                    name: client.name,
                                                    descriptionSv:
                                                        client.description.sv,
                                                    descriptionEn:
                                                        client.description.en,
                                                    webServerRedirectUri: (
                                                        <a
                                                            href={
                                                                client.registeredRedirectUri
                                                            }
                                                            target="_blank"
                                                        >
                                                            {
                                                                client.registeredRedirectUri
                                                            }
                                                        </a>
                                                    ),
                                                    clientId: client.clientId
                                                }}
                                                keysText={createKeyTexts(text)}
                                                keysOrder={[
                                                    CLIENT_NAME,
                                                    DESCRIPTION_EN,
                                                    DESCRIPTION_SV,
                                                    CLIENT_REDIRECT,
                                                    CLIENT_ID
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
                                                    text.DeleteClient +
                                                    " " +
                                                    client.name +
                                                    "?",
                                                confirmButtonText: text.Delete,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    deleteClient(client.id)
                                                        .then(response => {
                                                            toastOpen({
                                                                text:
                                                                    text.DeleteSuccessfully +
                                                                    client.name
                                                            });
                                                            redirectTo(
                                                                "/clients"
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

export default ShowClientDetails;
