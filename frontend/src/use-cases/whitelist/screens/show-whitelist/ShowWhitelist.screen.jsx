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

const ShowWhitelist = ({ whitelist }) => (
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
        <DigitDesign.Link to="/whitelist/add">
          <DigitFAB icon={Add} secondary />
        </DigitDesign.Link>
      </DigitLayout.Fill>
    )}
  />
);

export default ShowWhitelist;
