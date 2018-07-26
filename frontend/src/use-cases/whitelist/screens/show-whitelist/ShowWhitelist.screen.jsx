import React from "react";
import { Fill } from "../../../../common-ui/layout";
import GammaTable from "../../../../common/views/gamma-table";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Add } from "@material-ui/icons";
import { GammaLink } from "../../../../common-ui/design";

const ShowWhitelist = ({ whitelist, text }) => (
  <Fill>
    <GammaTable
      idProp="id"
      startOrderBy="cid"
      columnsOrder={["id", "cid"]}
      headerTexts={{
        id: "Id",
        cid: "Cid",
        __link: "Detaljer"
      }}
      data={whitelist.map(whitelistItem => {
        return {
          ...whitelistItem,
          __link: "/whitelist/" + whitelistItem.id
        };
      })}
    />
    <GammaLink to="/whitelist/add">
      <GammaFABButton component={Add} secondary />
    </GammaLink>
  </Fill>
);

export default ShowWhitelist;
