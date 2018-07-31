import React from "react";
import { Fill } from "../../../../common-ui/layout";
import GammaTable from "../../../../common/views/gamma-table";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Add } from "@material-ui/icons";
import { GammaLink } from "../../../../common-ui/design";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./ShowWhitelist.screen.translations.json";

const ShowWhitelist = ({ whitelist }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Whitelist.Screen.ShowWhitelist"
    render={text => (
      <Fill>
        <GammaTable
          titleText={text.Whitelist}
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
          <GammaFABButton component={Add} secondary />
        </GammaLink>
      </Fill>
    )}
  />
);

export default ShowWhitelist;
