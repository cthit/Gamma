import { TOAST_OPEN } from "../actions/toastActions";

export function toast(state = {}, action) {
  switch (action.type) {
    case TOAST_OPEN:
      return action.payload;
    default:
      return state;
  }
}
