import React from "react";
import { Switch, Route } from "react-router-dom";

import { Fill } from "../../common-ui/layout";
import GammaTable from "../../common/views/gamma-table";
import ShowAllUsers from "./screens/show-all-users";

class Users extends React.Component {
  constructor(props) {
    super();

    props.usersLoad();
  }

  render() {
    return (
      <Fill>
        <Switch>
          <Route path="/" exact component={ShowAllUsers} />
        </Switch>
      </Fill>
    );
  }
}

export default Users;
