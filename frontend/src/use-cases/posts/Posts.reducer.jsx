import _ from "lodash";

import {
    POSTS_LOAD_SUCCESSFULLY,
    POSTS_LOAD_USAGE_SUCCESSFULLY
} from "../../api/posts/actions.posts.api";

import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";

export function posts(state = [], action) {
    switch (action.type) {
        case POSTS_LOAD_SUCCESSFULLY:
            console.log(action.payload);
            return [...action.payload];
        case POSTS_LOAD_USAGE_SUCCESSFULLY:
            console.log(action.payload);
            const index = _.findIndex(state, {
                id: action.payload.postId
            });
            const newState = [...state];
            newState[index] = {
                ...state[index],
                usages: action.payload.data
            };
            return newState;
        case USER_LOGOUT_SUCCESSFULLY:
            return [];
        default:
            return state;
    }
}
