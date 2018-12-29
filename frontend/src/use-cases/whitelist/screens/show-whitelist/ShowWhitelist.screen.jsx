import {
    DigitFAB,
    DigitTable,
    DigitTranslations,
    DigitDesign,
    DigitLayout
} from "@cthit/react-digit-components";
import { Add } from "@material-ui/icons";
import React from "react";
import translations from "./ShowWhitelist.screen.translations.json";

class ShowWhitelist extends React.Component {
    componentDidMount() {
        const { whitelistLoad, gammaLoadingFinished } = this.props;

        whitelistLoad().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const { whitelist } = this.props;

        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Whitelist.Screen.ShowWhitelist"
                render={text => (
                    <DigitLayout.Fill>
                        <DigitTable
                            titleText={text.Whitelist}
                            searchText={text.SearchForWhitelistItem}
                            idProp="id"
                            startOrderBy="cid"
                            columnsOrder={["id", "cid"]}
                            headerTexts={{
                                id: text.Id,
                                cid: text.Cid,
                                __link: text.Details
                            }}
                            data={whitelist.map(whitelistItem => {
                                return {
                                    ...whitelistItem,
                                    __link: "/whitelist/" + whitelistItem.id
                                };
                            })}
                            emptyTableText={text.EmptyWhitelist}
                        />
                        <DigitLayout.DownRightPosition>
                            <DigitDesign.Link to="/whitelist/add">
                                <DigitFAB icon={Add} secondary />
                            </DigitDesign.Link>
                        </DigitLayout.DownRightPosition>
                    </DigitLayout.Fill>
                )}
            />
        );
    }
}

export default ShowWhitelist;
