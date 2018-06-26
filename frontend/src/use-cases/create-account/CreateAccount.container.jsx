import { connect } from "react-redux";
import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import CreateAccount from "./CreateAccount";

import loadTranslations from "../../common/utils/loaders/translations.loader";
import translations from "./CreateAccount.translations.json";

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
