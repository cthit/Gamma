import { connect } from "react-redux";

import LoginForm from "./LoginForm.view";
import translations from "./LoginForm.view.translations.json";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.LoginForm,
    "Login.View.LoginForm."
  )
});

const mapDispatchToProps = dispatch => ({
  toastOpen: data => dispatch(toastOpen(data)),
  redirectTo: path => dispatch(redirectTo(path))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginForm);
