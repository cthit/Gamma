export const TOAST_OPEN = "toast-open";

export function toastOpen(toastOptions) {
  return {
    type: TOAST_OPEN,
    error: false,
    payload: toastOptions
  };
}
