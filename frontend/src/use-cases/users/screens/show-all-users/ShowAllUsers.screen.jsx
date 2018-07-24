import React from "react";

import GammaTable from "../../../../common/views/gamma-table";

const ShowAllUsers = ({ users, text }) => (
  <GammaTable
    idProp="cid"
    startOrderBy="firstName"
    columnsOrder={["firstName", "nick", "lastName", "cid", "acceptanceYear"]}
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
  />
);

export default ShowAllUsers;
