import React from "react";

import { Fill } from "../../../../common-ui/layout";
import GammaTable from "../../../../common/views/gamma-table";
import { GammaLink } from "../../../../common-ui/design";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Add } from "@material-ui/icons";

const ShowAllWebsites = ({ websites }) => (
  <Fill>
    <GammaTable
      idProp="id"
      startOrderBy="name"
      columnsOrder={["id", "name", "pretty_name"]}
      headerTexts={{
        id: "Id",
        name: "Name",
        pretty_name: "Pretty name",
        __link: "Detaljer"
      }}
      data={websites.map(website => {
        return {
          ...website,
          __link: "/websites/" + website.id
        };
      })}
      emptyTableText="Det finns inga hemsidor"
    />
    <GammaLink to="/websites/add">
      <GammaFABButton component={Add} secondary />
    </GammaLink>
  </Fill>
);

export default ShowAllWebsites;
