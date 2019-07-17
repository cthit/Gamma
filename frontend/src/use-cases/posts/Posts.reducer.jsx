import _ from "lodash";

import {
    POST_GET_SUCCESSFULLY,
    POSTS_LOAD_SUCCESSFULLY,
    POSTS_LOAD_USAGE_SUCCESSFULLY
} from "../../api/posts/actions.posts.api";

import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";

export function posts(state = [], action) {
    switch (action.type) {
        case POSTS_LOAD_SUCCESSFULLY:
            return [...action.payload];
        case POST_GET_SUCCESSFULLY:
            const indexPGS = findIndexById(state, action.payload.data.id);
            if (indexPGS === -1) {
                return [...state, action.payload.data];
            } else {
                const newState = [...state];
                newState[indexPGS] = {
                    ...state[indexPGS],
                    ...action.payload.data
                };
                return newState;
            }
        case POSTS_LOAD_USAGE_SUCCESSFULLY:
            const postId = action.payload.postId;
            const indexPLUS = findIndexById(state, postId);
            if (indexPLUS === -1) {
                return [
                    ...state,
                    {
                        id: action.payload.postId,
                        usages: action.payload.data
                    }
                ];
            } else {
                const newState = [...state];
                newState[indexPLUS] = {
                    ...state[indexPLUS],
                    usages: action.payload.data
                };
                return newState;
            }
        case USER_LOGOUT_SUCCESSFULLY:
            return [];
        default:
            return state;
    }
}

function findIndexById(state, postId) {
    return _.findIndex(state, {
        id: postId
    });
}
