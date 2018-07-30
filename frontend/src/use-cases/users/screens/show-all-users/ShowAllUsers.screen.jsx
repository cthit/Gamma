import React from "react";

import GammaTable from "../../../../common/views/gamma-table";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./ShowAllUsers.screen.translations.json";

const ShowAllUsers = ({ users }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Users.Screen.ShowAllUsers"
    render={text => (
      <GammaTable
        idProp="cid"
        startOrderBy="firstName"
        columnsOrder={[
          "firstName",
          "nick",
          "lastName",
          "cid",
          "acceptanceYear"
        ]}
        headerTexts={{
          firstName: text.FirstName,
          lastName: text.lastName,
          cid: text.Cid,
          nick: text.Nick,
          acceptanceYear: text.AcceptanceYear,
          __link: text.Details
        }}
        data={users.map(user => {
          return { ...user, __link: "/users/" + user.cid };
        })}
        emptyTableText="Det finns inga användare, wait hur är du inloggad?"
      />
    )}
  />
);

export default ShowAllUsers;
