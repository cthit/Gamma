export const REDIRECT_TO = "redirect_to";
export const REDIRECT_FINISHED = "redirect_finished";

export function redirectTo(path) {
  return {
    type: REDIRECT_TO,
    error: false,
    payload: {
      path: path
    }
  };
}

export function redirectFinished() {
  return {
    type: REDIRECT_FINISHED,
    error: false,
    payload: null
  };
}
