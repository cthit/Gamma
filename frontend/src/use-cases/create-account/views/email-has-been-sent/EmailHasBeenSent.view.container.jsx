import { connect } from "react-redux";
import EmailHasBeenSent from "./EmailHasBeenSent.view";

import loadTranslations from "../../../../common/utils/loadTranslations";
import translations from "./EmailHasBeenSent.view.translations.json";

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
