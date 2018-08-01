import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import {
  POSTS_LOAD_SUCCESSFULLY,
  POSTS_LOAD_FAILED,
  POSTS_ADD_SUCCESSFULLY,
  POSTS_ADD_FAILED,
  POSTS_CHANGE_SUCCESSFULLY,
  POSTS_CHANGE_FAILED,
  POSTS_DELETE_SUCCESSFULLY,
  POSTS_DELETE_FAILED
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
        .post("http://localhost:8081/admin/groups/posts", post, {
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

export function postsChange(post, postId) {
  console.log(post);
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .put("http://localhost:8081/admin/groups/posts/" + postId, post, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(postsChangeSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(postsChangeFailed(error));
          reject(error);
        });
    });
  };
}

export function postsDelete(postId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .delete("http://localhost:8081/admin/groups/posts/" + postId, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(postsDeleteSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(postsDeleteFailed(error));
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

function postsChangeSuccessfully() {
  return {
    type: POSTS_CHANGE_SUCCESSFULLY,
    error: false
  };
}

function postsChangeFailed(error) {
  return {
    type: POSTS_CHANGE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function postsDeleteSuccessfully() {
  return {
    type: POSTS_DELETE_SUCCESSFULLY,
    error: false
  };
}

function postsDeleteFailed(error) {
  return {
    type: POSTS_DELETE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}
