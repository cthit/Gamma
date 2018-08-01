import {
  GAMMA_DIALOG_OPEN,
  GAMMA_DIALOG_CLOSED_CONFIRM,
  GAMMA_DIALOG_CLOSED_CANCEL
} from "./GammaDialog.view.actions";

/*
 * Options
 *  - title
 *  - description
 *  - cancelButtonText
 *  - confirmButtonText
 */
export function gammaDialogOpen(options) {
  return {
    type: GAMMA_DIALOG_OPEN,
    error: false,
    payload: {
      options: options
    }
  };
}

export function gammaDialogClosedConfirm() {
  return {
    type: GAMMA_DIALOG_CLOSED_CONFIRM,
    error: false
  };
}

export function gammaDialogClosedCancel() {
  return {
    type: GAMMA_DIALOG_CLOSED_CANCEL,
    error: false
  };
}
