import { TOAST_OPEN } from "./GammaToast.view.actions";

export function toastOpen(toastOptions) {
  return {
    type: TOAST_OPEN,
    error: false,
    payload: toastOptions
  };
}
