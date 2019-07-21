import React from "react";
import {
    DigitDesign,
    DigitFAB,
    DigitLayout,
    DigitTable,
    DigitTranslations
} from "@cthit/react-digit-components";
import translations from "./ShowAllClients.screen.translations.json";
import {
    CLIENT_ID,
    CLIENT_NAME,
    CLIENT_REDIRECT
} from "../../../../api/clients/props.clients.api";
import { Add } from "@material-ui/icons";
function generateHeaderTexts(text) {
    const headerTexts = {};

    headerTexts[CLIENT_NAME] = text.Name;
    headerTexts[CLIENT_ID] = text.ClientId;
    headerTexts[CLIENT_REDIRECT] = text.RedirectURI;
    headerTexts["__link"] = text.Details;
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
            <>
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <DigitLayout.Fill>
                            <DigitTable
                                titleText={text.Clients}
                                searchText={text.SearchForClients}
                                idProp={CLIENT_ID}
                                startOrderBy="sv"
                                columnsOrder={[
                                    CLIENT_NAME,
                                    CLIENT_ID,
                                    CLIENT_REDIRECT
                                ]}
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
                <DigitLayout.DownRightPosition>
                    <DigitDesign.Link to="/clients/new">
                        <DigitFAB icon={Add} secondary />
                    </DigitDesign.Link>
                </DigitLayout.DownRightPosition>
            </>
        );
    }
}

export default ShowAllClients;
