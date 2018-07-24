import { USERS_LOAD_SUCCESSFULLY } from "./Users.actions";

export function users(state = [], action) {
  switch (action.type) {
    case USERS_LOAD_SUCCESSFULLY:
      return Object.keys(action.payload).map(index => action.payload[index]);
    default:
      return state;
  }
}
