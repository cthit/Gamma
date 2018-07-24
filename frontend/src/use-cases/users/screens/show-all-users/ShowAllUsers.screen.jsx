import React from "react";

import GammaTable from "../../../../common/views/gamma-table";

const ShowAllUsers = ({ users }) => (
  <div>
    {console.log(users)}
    <GammaTable
      idProp="cid"
      startOrderBy="firstName"
      columnsOrder={["firstName", "nick", "lastName", "cid", "acceptanceYear"]}
      headerTexts={{
        firstName: "First name",
        lastName: "Last name",
        cid: "Cid",
        nick: "Nick",
        acceptanceYear: "Acceptance Year",
        __link: "Detaljer"
      }}
      data={users.map(user => {
        return { ...user, __link: "/users/" + user.cid };
      })}
    />
  </div>
);

export default ShowAllUsers;
