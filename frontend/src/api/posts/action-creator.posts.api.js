import { deletePost } from "./delete.posts.api";
import { getPosts, getPostUsage } from "./get.posts.api";
import { addPost } from "./post.posts.api";
import { editPost } from "./put.posts.api";
import {
    POSTS_ADD_FAILED,
    POSTS_ADD_SUCCESSFULLY,
    POSTS_CHANGE_FAILED,
    POSTS_CHANGE_SUCCESSFULLY,
    POSTS_DELETE_FAILED,
    POSTS_DELETE_SUCCESSFULLY,
    POSTS_LOAD_FAILED,
    POSTS_LOAD_LOADING,
    POSTS_LOAD_SUCCESSFULLY,
    POSTS_LOAD_USAGE_FAILED,
    POSTS_LOAD_USAGE_SUCCESSFULLY
} from "./actions.posts.api";

import { requestPromise } from "../utils/requestPromise";

export function createGetPostsAction() {
    return requestPromise(
        getPosts,
        createGetPostsLoadingAction,
        createGetPostsSuccessfullyAction,
        createGetPostsFailedAction
    );
}

export function createAddPostAction(postData) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            addPost(postData)
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

export function createEditPostAction(postData, postId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editPost(postId, postData)
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

export function createDeletePostAction(postId) {
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

export function createGetPostUsageAction(postId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            getPostUsage(postId)
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

function createGetPostsLoadingAction() {
    return {
        type: POSTS_LOAD_LOADING,
        error: false
    };
}

function createGetPostsFailedAction(error) {
    return {
        type: POSTS_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function createGetPostsSuccessfullyAction(response) {
    return { type: POSTS_LOAD_SUCCESSFULLY, payload: [...response.data] };
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
