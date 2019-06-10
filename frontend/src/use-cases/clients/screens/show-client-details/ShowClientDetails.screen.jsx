import React from 'react';
import {
    DigitButton,
    DigitDisplayData,
    DigitIfElseRendering,
    DigitTranslations,
    DigitLayout,
    DigitDesign
} from "@cthit/react-digit-components";
import translations from "./ShowClientDetails.screen.translations.json";
import {
    CLIENT_NAME,
    CLIENT_REDIRECT,
    DESCRIPTION_EN,
    DESCRIPTION_SV,
    CLIENT_ID
} from "../../../../api/clients/props.clients.api";

function createKeyTexts(text) {
    const output = {};

    output[CLIENT_NAME] = text.Name;
    output[DESCRIPTION_EN] = text.DescriptionEn;
    output[DESCRIPTION_SV] = text.DescriptionSv;
    output[CLIENT_REDIRECT] = text.Redirect;
    output[CLIENT_ID] = text.ClientId;
    return(output);
}
class ShowClientDetails extends React.Component {
    constructor(props) {
        super(props);

        const {
            getClient,
            client,
            gammaLoadingFinished
        } = this.props;

        Promise.all([
            getClient(client)
            ]).then(() => {
                gammaLoadingFinished();
        });
    }

    render() {
        const { client } = this.props;
        console.log(client);
        return (
            <DigitIfElseRendering
            test={      // Lägg till conditions för att kolla så värden inte är null.
                client != null
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
                            text={client.name}
                            />
                            <DigitDesign.CardBody>
                                <DigitDisplayData
                                    data={{
                                        name: client.name,
                                        descriptionSv: client.description.sv,
                                        descriptionEn: client.description.en,
                                        webServerRedirectUri: <a
                                            href={client.registeredRedirectUri}
                                            target="_blank"
                                        >
                                            {client.registeredRedirectUri}
                                        </a>,
                                        clientId: client.clientId,
                                    }}
                                    keysText={createKeyTexts(text)}
                                    keysOrder={[
                                        CLIENT_NAME,
                                        DESCRIPTION_EN,
                                        DESCRIPTION_SV,
                                        CLIENT_REDIRECT,
                                        //CLIENT_ID     // Breaks the alignment, should be fixed in digIT React components
                                    ]}
                                />
                            </DigitDesign.CardBody>
                        </DigitDesign.Card>
                    </DigitLayout.Column>
                )}/>
            )}/>
        )
    }
}

export default ShowClientDetails;