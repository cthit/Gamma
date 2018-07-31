import React from "react";
import GammaTable from "../../../../common/views/gamma-table";
import { Fill } from "../../../../common-ui/layout";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Add } from "@material-ui/icons";
import { GammaLink } from "../../../../common-ui/design";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./ShowAllGroups.screen.translations.json";

const ShowAllGroups = ({ groups }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Groups.Screen.ShowAllGroups"
    render={(text, activeLanguage) => (
      <Fill>
        <GammaTable
          titleText={text.Groups}
          searchText={text.SearchForGroups}
          idProp="id"
          startOrderBy="name"
          columnsOrder={["id", "name", "description", "email", "func", "type"]}
          headerTexts={{
            id: text.Id,
            name: text.Name,
            description: text.Description,
            email: text.Email,
            func: text.Function,
            type: text.Type,
            __link: text.Details
          }}
          data={groups.map(group => {
            return {
              id: group.id,
              name: group.name,
              description: group.description.sv,
              email: group.email,
              func: group.func.sv,
              type: _getTypeText(group.type, text),
              __link: "/groups/" + group.id
            };
          })}
          emptyTableText={text.NoGroups}
        />
        <GammaLink to="/groups/new">
          <GammaFABButton component={Add} secondary />
        </GammaLink>
      </Fill>
    )}
  />
);

function _getTypeText(type, text) {
  switch (type) {
    case "SOCIETY":
      return text.society;
    case "COMMITTEE":
      return text.Committee;
    case "BOARD":
      return text.Board;
    default:
      return "Unknown";
  }
}

export default ShowAllGroups;
