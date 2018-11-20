import {
    DigitFAB,
    DigitTable,
    DigitTranslations
} from "@cthit/react-digit-components";
import { Add } from "@material-ui/icons";
import React from "react";
import { GammaLink } from "../../../../common-ui/design";
import { Fill } from "../../../../common-ui/layout";
import translations from "./ShowWhitelist.screen.translations.json";

const ShowWhitelist = ({ whitelist }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Whitelist.Screen.ShowWhitelist"
        render={text => (
            <Fill>
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
                <GammaLink to="/whitelist/add">
                    <DigitFAB icon={Add} secondary />
                </GammaLink>
            </Fill>
        )}
    />
);

export default ShowWhitelist;
