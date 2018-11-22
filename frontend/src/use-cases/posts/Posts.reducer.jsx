import {
  POSTS_LOAD_SUCCESSFULLY,
  POSTS_LOAD_USAGE_SUCCESSFULLY
} from "./Posts.actions";

import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";

export function posts(state = [], action) {
  switch (action.type) {
    case POSTS_LOAD_SUCCESSFULLY:
      return [...action.payload];
    case POSTS_LOAD_USAGE_SUCCESSFULLY:
      console.log(action.payload);
      //TODO find postId with action.payload.postId and insert action.payload.data
      break;
    case USER_LOGOUT_SUCCESSFULLY:
      return [];
    default:
      return state;
  }
}
