import React from "react";
import GammaTable from "../../../../common/views/gamma-table";
import { Fill } from "../../../../common-ui/layout";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Add } from "@material-ui/icons";
import { GammaLink } from "../../../../common-ui/design";

const ShowAllGroups = ({ groups }) => (
  <Fill>
    <GammaTable
      idProp="id"
      startOrderBy="name"
      columnsOrder={["id", "name", "description", "email", "func"]}
      headerTexts={{
        id: "Id",
        name: "Namn",
        description: "Beskrivning",
        email: "Email",
        func: "Function",
        __link: "Detaljer"
      }}
      data={groups.map(group => {
        return {
          id: group.id,
          name: group.name,
          description: group.description.sv,
          email: group.email,
          func: group.func.sv,
          __link: "/groups/" + group.id
        };
      })}
      emptyTableText="Det finns inga grupper"
    />
    <GammaLink to="/groups/new">
      <GammaFABButton component={Add} secondary />
    </GammaLink>
  </Fill>
);

export default ShowAllGroups;
