import React from "react";

import { Fill } from "../../common-ui/layout";
import GammaTable from "../../common/views/gamma-table";

class Users extends React.Component {
  constructor(props) {
    super();

    props.usersLoad();

    this.state = {
      selected: []
    };
  }

  render() {
    const { users } = this.props;
    const { selected } = this.state;

    console.log(users);

    return (
      <Fill>
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
            firstName: "First name",
            lastName: "Last name",
            cid: "Cid",
            nick: "Nick",
            acceptanceYear: "Acceptance Year"
          }}
          data={users}
          selected={selected}
          onSelectedUpdated={newSelected =>
            this.setState({
              selected: newSelected
            })
          }
        />
      </Fill>
    );
  }
}

export default Users;
