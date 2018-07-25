import { connect } from "react-redux";
import translations from "./Login.translations.json";
import Login from "./Login";

import loadTranslations from "../../common/utils/loaders/translations.loader";

import { login } from "./Login.action-creator";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(state.localize, translations, "Login.")
});

const mapDispatchToProps = dispatch => ({
  redirectTo: path => dispatch(redirectTo(path)),
  login: (data, persistant) => dispatch(login(data, persistant))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);
