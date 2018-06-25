import { TOAST_OPEN } from "./GammaToast.actions";

export function toastOpen(toastOptions) {
  return {
    type: TOAST_OPEN,
    error: false,
    payload: toastOptions
  };
}
