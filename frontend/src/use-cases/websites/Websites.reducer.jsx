import {
  WEBSITES_LOAD_SUCCESSFULLY,
  WEBSITES_LOAD_FAILED,
  WEBSITES_ADD_SUCCESSFULLY,
  WEBSITES_ADD_FAILED,
  WEBSITES_DELETE_SUCCESSFULLY,
  WEBSITES_DELETE_FAILED,
  WEBSITES_CHANGE_SUCCESSFULLY,
  WEBSITES_CHANGE_FAILED
} from "./Websites.actions";

export function websites(state = [], action) {
  switch (action.type) {
    case WEBSITES_LOAD_SUCCESSFULLY:
      return [...action.payload.websites];
    default:
      return state;
  }
}
