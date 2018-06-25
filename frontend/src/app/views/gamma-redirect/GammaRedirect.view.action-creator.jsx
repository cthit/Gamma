import { REDIRECT_TO, REDIRECT_FINISHED } from "./GammaRedirect.view.actions";

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
