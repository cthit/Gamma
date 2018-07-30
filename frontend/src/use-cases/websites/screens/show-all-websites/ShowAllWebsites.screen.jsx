import React from "react";

import { Fill } from "../../../../common-ui/layout";
import GammaTable from "../../../../common/views/gamma-table";
import { GammaLink } from "../../../../common-ui/design";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Add } from "@material-ui/icons";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./ShowAllWebsites.screen.translations.json";

const ShowAllWebsites = ({ websites }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Websites.Screen.ShowAllWebsites"
    render={text => (
      <Fill>
        <GammaTable
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
          <GammaFABButton component={Add} secondary />
        </GammaLink>
      </Fill>
    )}
  />
);

export default ShowAllWebsites;
