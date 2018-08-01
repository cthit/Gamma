import {
  GAMMA_DIALOG_OPEN,
  GAMMA_DIALOG_CLOSED_CONFIRM,
  GAMMA_DIALOG_CLOSED_CANCEL
} from "./GammaDialog.view.actions";

export function dialog(state = null, action) {
  switch (action.type) {
    case GAMMA_DIALOG_OPEN:
      return {
        ...action.payload.options,
        open: true
      };
    case GAMMA_DIALOG_CLOSED_CONFIRM:
      return {
        ...state,
        open: false
      };
    case GAMMA_DIALOG_CLOSED_CANCEL:
      return {
        ...state,
        open: false
      };
    default:
      return state;
  }
}
