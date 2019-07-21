import React from "react";
import {
    DigitDesign,
    DigitFAB,
    DigitLayout,
    DigitTable,
    DigitTranslations
} from "@cthit/react-digit-components";
import translations from "./ShowAllApiKeys.screen.translations.json";
import {
    API_KEY_NAME,
    DESCRIPTION,
    API_KEY_ID
} from "../../../../api/api-keys/props.api-keys.api";
import { Add } from "@material-ui/icons";
import {
    CLIENT_ID,
    CLIENT_NAME,
    CLIENT_REDIRECT
} from "../../../../api/clients/props.clients.api";

function generateHeaderTexts(text) {
    const headerTexts = {};

    headerTexts[API_KEY_ID] = text.Id;
    headerTexts[API_KEY_NAME] = text.Name;
    headerTexts["__link"] = text.ShowApiKey;

    return headerTexts;
}

class ShowAllApiKeys extends React.Component {
    componentDidMount() {
        const { getApiKeys, gammaLoadingFinished } = this.props;
        getApiKeys().then(() => {
            gammaLoadingFinished();
        });
    }

    render() {
        const { apiKeys } = this.props;
        console.log(translations);
        return (
            <>
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <DigitLayout.Fill>
                            <DigitTable
                                titleText={text.ApiKeys}
                                searchText={text.SearchForApiKeys}
                                idProp={API_KEY_ID}
                                startOrderBy="sv"
                                columnsOrder={[API_KEY_NAME, API_KEY_ID]}
                                headerTexts={generateHeaderTexts(text)}
                                data={apiKeys.map(apiKey => {
                                    return {
                                        ...apiKey,
                                        __link: "/api_keys/" + apiKey.id
                                    };
                                })}
                                emptyTableText={text.NoClients}
                            />
                        </DigitLayout.Fill>
                    )}
                />
                <DigitLayout.DownRightPosition>
                    <DigitDesign.Link to="/api_keys/new">
                        <DigitFAB icon={Add} secondary />
                    </DigitDesign.Link>
                </DigitLayout.DownRightPosition>
            </>
        );
    }
}

export default ShowAllApiKeys;
