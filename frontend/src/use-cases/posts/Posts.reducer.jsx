import { POSTS_LOAD_SUCCESSFULLY } from "./Posts.actions";

import { USER_LOGOUT_SUCCESSFULLY } from "../../app/elements/user-information/UserInformation.element.actions";

export function posts(state = [], action) {
  switch (action.type) {
    case POSTS_LOAD_SUCCESSFULLY:
      return [...action.payload];
    case USER_LOGOUT_SUCCESSFULLY:
      return [];
    default:
      return state;
  }
}
