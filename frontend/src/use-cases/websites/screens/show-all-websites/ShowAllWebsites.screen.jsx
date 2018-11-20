import React from "react";

import { Fill } from "../../../../common-ui/layout";
import GammaTable from "../../../../common/views/gamma-table";
import { GammaLink } from "../../../../common-ui/design";
import { Add } from "@material-ui/icons";
import { DigitTranslations, DigitFAB } from "@cthit/react-digit-components";
import translations from "./ShowAllWebsites.screen.translations.json";

const ShowAllWebsites = ({ websites }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Websites.Screen.ShowAllWebsites"
        render={text => (
            <Fill>
                <GammaTable
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
                <GammaLink to="/websites/add">
                    <DigitFAB component={Add} secondary />
                </GammaLink>
            </Fill>
        )}
    />
);

export default ShowAllWebsites;
