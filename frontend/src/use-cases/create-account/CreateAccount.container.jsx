import { connect } from "react-redux";
import translations from "./CreateAccount.translations.json";
import CreateAccount from "./CreateAccount";

import loadTranslations from "../../common/utils/loaders/translations.loader";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(state.localize, translations, "CreateAccount.")
});

const mapDispatchToProps = dispatch => ({
  redirectTo: path => dispatch(redirectTo(path))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CreateAccount);
