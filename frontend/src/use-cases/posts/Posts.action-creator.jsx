import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import { POST_LOAD_SUCCESSFULLY } from "./Posts.actions";

export function postsLoad() {
  return dispatch => {
    axios.get("http://localhost/admin/groups/posts", {
      headers: {
        Authorization: "Bearer " + token()
      }
    });
  };
}
