import React from "react";
import { Switch, Route } from "react-router-dom";

import { Fill } from "../../common-ui/layout";
import GammaTable from "../../common/views/gamma-table";
import ShowAllUsers from "./screens/show-all-users";
import ShowUserDetails from "./screens/show-user-details";

class Users extends React.Component {
  constructor(props) {
    super();

    props.usersLoad();
  }

  render() {
    return (
      <Fill>
        <Switch>
          <Route path="/users" exact component={ShowAllUsers} />
          <Route path="/users/:cid" component={ShowUserDetails} />
        </Switch>
      </Fill>
    );
  }
}

export default Users;
