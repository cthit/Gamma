import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import { USERS_LOAD, USERS_LOAD_SUCCESSFULLY } from "./Users.actions";

export function usersLoad() {
  return dispatch => {
    axios
      .get("http://localhost:8081/users", {
        headers: {
          Authorization: "Bearer " + token()
        }
      })
      .then(response => dispatch(usersLoadSuccessfully(response.data)))
      .catch(error => console.log(error));
  };
}

function usersLoadSuccessfully(data) {
  return {
    type: USERS_LOAD_SUCCESSFULLY,
    error: false,
    payload: {
      ...data
    }
  };
}
