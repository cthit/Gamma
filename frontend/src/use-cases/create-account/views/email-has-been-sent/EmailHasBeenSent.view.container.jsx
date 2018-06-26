import { connect } from "react-redux";

import EmailHasBeenSent from "./EmailHasBeenSent.view";
import translations from "./EmailHasBeenSent.view.translations.json";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.EmailHasBeenSent,
    "CreateAccount.View.EmailHasBeenSent."
  )
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EmailHasBeenSent);
