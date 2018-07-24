import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import { POSTS_LOAD_SUCCESSFULLY } from "./Posts.actions";

export function postsLoad() {
  return dispatch => {
    axios
      .get("http://localhost:8081/admin/groups/posts", {
        headers: {
          Authorization: "Bearer " + token()
        }
      })
      .then(response => dispatch(postsLoadSuccessfully(response.data)));
  };
}

export function postsAdd(post) {
  return dispatch => {
    axios
      .post("http://localhost:8081/admin/groups/add_post", post, {
        headers: {
          Authorization: "Bearer " + token()
        }
      })
      .then(response => {
        console.log("yay");
      });
  };
}

function postsLoadSuccessfully(data) {
  return { type: POSTS_LOAD_SUCCESSFULLY, payload: [...data] };
}
