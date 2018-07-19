import { connect } from "react-redux";

import LoginForm from "./LoginForm.view";
import translations from "./LoginForm.view.translations.json";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.LoginForm,
    "Login.View.LoginForm."
  )
});

const mapDispatchToProps = dispatch => ({
  showError: text => dispatch(toastOpen({ text: text }))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginForm);
