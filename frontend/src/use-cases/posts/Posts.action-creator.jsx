import { deletePost } from "../../api/posts/delete.posts.api";
import { getPosts } from "../../api/posts/get.posts.api";
import { addPost } from "../../api/posts/post.posts.api";
import { editPost } from "../../api/posts/put.posts.api";
import {
  POSTS_ADD_FAILED,
  POSTS_ADD_SUCCESSFULLY,
  POSTS_CHANGE_FAILED,
  POSTS_CHANGE_SUCCESSFULLY,
  POSTS_DELETE_FAILED,
  POSTS_DELETE_SUCCESSFULLY,
  POSTS_LOAD_FAILED,
  POSTS_LOAD_SUCCESSFULLY,
  POSTS_LOAD_USAGE_FAILED,
  POSTS_LOAD_USAGE_SUCCESSFULLY
} from "./Posts.actions";

export function postsLoad() {
  return dispatch => {
    return new Promise((resolve, reject) => {
      getPosts()
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
      addPost(post)
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
  return dispatch => {
    return new Promise((resolve, reject) => {
      editPost(postId, post)
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
      deletePost(postId)
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

export function postsLoadUsage(postId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      deletePost(postId)
        .then(response => {
          dispatch(postsLoadUsageSuccessfully(response.data, postId));
          resolve(response.data);
        })
        .catch(error => {
          dispatch(postsLoadUsageFailed());
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

function postsLoadUsageSuccessfully(data, postId) {
  return {
    type: POSTS_LOAD_USAGE_SUCCESSFULLY,
    error: false,
    payload: {
      data: data,
      postId: postId
    }
  };
}

function postsLoadUsageFailed(error) {
  return {
    type: POSTS_LOAD_USAGE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}
