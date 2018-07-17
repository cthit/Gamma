import { connect } from "react-redux";
import translations from "./SignIn.translations.json";
import SignIn from "./SignIn";

import loadTranslations from "../../common/utils/loaders/translations.loader";

import { signIn } from "./SignIn.action-creator";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(state.localize, translations, "SignIn.")
});

const mapDispatchToProps = dispatch => ({
  redirectTo: path => dispatch(redirectTo(path)),
  signIn: (data, persistant, successMsg, errorMsg, networkErrorMsg) =>
    dispatch(signIn(data, persistant, successMsg, errorMsg, networkErrorMsg))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SignIn);
