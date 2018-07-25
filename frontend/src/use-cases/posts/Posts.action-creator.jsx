import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import {
  POSTS_LOAD_SUCCESSFULLY,
  POSTS_LOAD_FAILED,
  POSTS_ADD_SUCCESSFULLY,
  POSTS_ADD_FAILED
} from "./Posts.actions";

export function postsLoad() {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .get("http://localhost:8081/admin/groups/posts", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(postsLoadSuccessfully(response.data));
          resolve(response);
        })
        .catch(error => {
          postsLoadFailed(error);
          reject(error);
        });
    });
  };
}

export function postsAdd(post) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .post("http://localhost:8081/admin/groups/add_post", post, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(postsAddSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(postsAddFailed(error));
          reject(error);
        });
    });
  };
}

function postsAddSuccessfully() {
  return {
    type: POSTS_ADD_SUCCESSFULLY,
    error: false
  };
}

function postsAddFailed(error) {
  return {
    type: POSTS_ADD_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function postsLoadFailed(error) {
  return {
    type: POSTS_LOAD_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function postsLoadSuccessfully(data) {
  return { type: POSTS_LOAD_SUCCESSFULLY, payload: [...data] };
}
