import {
  GROUPS_LOAD_SUCCESSFULLY,
  GROUPS_LOAD_FAILED,
  GROUPS_ADD_SUCCESSFULLY,
  GROUPS_ADD_FAILED,
  GROUPS_REMOVE_SUCCESSFULLY,
  GROUPS_REMOVE_FAILED,
  GROUPS_CHANGE_SUCCESSFULLY,
  GROUPS_CHANGE_FAILED,
  GROUPS_ADD_USER_SUCCESSFULLY,
  GROUPS_ADD_USER_FAILED,
  GROUPS_REMOVE_USER_SUCCESSFULLY,
  GROUPS_REMOVE_USER_FAILED,
  GROUPS_CHANGE_POST_SUCCESSFULLY,
  GROUPS_CHANGE_POST_FAILED
} from "./Groups.actions";

export function groups(state = [], action) {
  switch (action.type) {
    case GROUPS_LOAD_SUCCESSFULLY:
      return [...action.payload.data];
    default:
      return state;
  }
}