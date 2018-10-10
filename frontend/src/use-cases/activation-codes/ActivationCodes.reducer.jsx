import {
  ACTIVATION_CODES_LOAD_SUCCESSFULLY,
  ACTIVATION_CODES_LOAD_FAILED
} from "./ActivationCodes.actions";

export function activationCodes(state = [], action) {
  switch (action.type) {
    case ACTIVATION_CODES_LOAD_SUCCESSFULLY:
      return [...action.payload.data];
    default:
      return state;
  }
}
