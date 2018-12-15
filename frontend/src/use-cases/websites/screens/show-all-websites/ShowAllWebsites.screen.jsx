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
                uniquePath="Websites.Screen.ShowAllWebsites"
                render={text => (
                    <DigitLayout.Fill>
                        <DigitTable
                            titleText={text.Websites}
                            searchText={text.SearchForWebsites}
                            idProp="id"
                            startOrderBy="name"
                            columnsOrder={["id", "name", "prettyName"]}
                            headerTexts={{
                                id: text.Id,
                                name: text.Name,
                                prettyName: text.PrettyName,
                                __link: text.Details
                            }}
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
