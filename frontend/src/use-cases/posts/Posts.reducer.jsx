import { POSTS_LOAD_SUCCESSFULLY } from "./Posts.actions";

export function posts(state = [], action) {
  switch (action.type) {
    case POSTS_LOAD_SUCCESSFULLY:
      return [...action.payload];
    default:
      return state;
  }
}
