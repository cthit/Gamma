import React from "react";
import {
    DigitLayout,
    DigitTable,
    DigitTranslations
} from "@cthit/react-digit-components";
import translations from "./ShowAllClients.screen.translations.json"
import {
    CLIENT_ID,
    CLIENT_NAME
} from "../../../../api/clients/props.clients.api"
function generateHeaderTexts(text) {
    const headerTexts = {};

    headerTexts[CLIENT_NAME] = text.Name;
    return headerTexts;
}

class ShowAllClients extends React.Component {
    componentDidMount() {
        const { getClients, gammaLoadingFinished } = this.props;
        getClients().then(() => {
            gammaLoadingFinished();
        });
    }
    render() {
        const { clients } = this.props;
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Clients.Screen.ShowAllClients"
                render={text => (
                    <DigitLayout.Fill>
                        <DigitTable
                            titleText={text.Clients}
                            searchText={text.SearchForClients}
                            idProp={CLIENT_ID}
                            startOrderBy="sv"
                            columnsOrder={[CLIENT_NAME, "sv", "en"]}
                            headerTexts={generateHeaderTexts(text)}
                            data={clients.map(client => {
                                return {
                                    ...client,
                                    __link: "/clients/" + client.id
                                };
                            })}
                            emptyTableText={text.NoClients}
                        />
                    </DigitLayout.Fill>
                )}
            />
        );
    }
}

export default ShowAllClients;
