import {
    DigitDesign,
    DigitFAB,
    DigitTable,
    DigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";
import { Add } from "@material-ui/icons";
import React, { Component } from "react";
import translations from "./ShowAllWebsites.screen.translations.json";
import {
    NAME,
    PRETTY_NAME,
    WEBSITE_ID
} from "../../../../api/websites/props.websites.api";

function generateHeaderTexts(text) {
    const headerTexts = {};

    headerTexts[WEBSITE_ID] = text.Id;
    headerTexts[NAME] = text.Name;
    headerTexts[PRETTY_NAME] = text.PrettyName;
    headerTexts["__link"] = text.Details;

    return headerTexts;
}

class ShowAllWebsites extends Component {
    componentDidMount() {
        const { getWebsites, gammaLoadingFinished } = this.props;

        getWebsites().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        let { websites } = this.props;
        return (
            <DigitTranslations
                translations={translations}
                render={text => (
                    <DigitLayout.Fill>
                        <DigitTable
                            titleText={text.Websites}
                            searchText={text.SearchForWebsites}
                            idProp={WEBSITE_ID}
                            startOrderBy={NAME}
                            columnsOrder={[WEBSITE_ID, NAME, PRETTY_NAME]}
                            headerTexts={generateHeaderTexts(text)}
                            data={websites.map(website => {
                                return {
                                    ...website,
                                    __link: "/websites/" + website.id
                                };
                            })}
                            emptyTableText={text.NoWebsites}
                        />
                        <DigitLayout.DownRightPosition>
                            <DigitDesign.Link to="/websites/add">
                                <DigitFAB icon={Add} secondary />
                            </DigitDesign.Link>
                        </DigitLayout.DownRightPosition>
                    </DigitLayout.Fill>
                )}
            />
        );
    }
}

export default ShowAllWebsites;
