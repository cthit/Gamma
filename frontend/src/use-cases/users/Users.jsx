import React from "react";
import { Switch, Route } from "react-router-dom";

import { Fill } from "../../common-ui/layout";
import ShowAllUsers from "./screens/show-all-users";
import ShowUserDetails from "./screens/show-user-details";
import EditUserDetails from "./screens/edit-user-details";

class Users extends React.Component {
  constructor(props) {
    super();

    props.usersLoad().then(response => {
      props.gammaLoadingFinished();
    });
  }

  render() {
    return (
      <Fill>
        <Switch>
          <Route path="/users" exact component={ShowAllUsers} />
          <Route path="/users/:cid" exact component={ShowUserDetails} />
          <Route path="/users/:cid/edit" exact component={EditUserDetails} />
        </Switch>
      </Fill>
    );
  }
}

export default Users;
