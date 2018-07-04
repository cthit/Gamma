import { connect } from "react-redux";

import CreationOfAccountFinished from "./CreationOfAccountFinished.view";
import translations from "./CreationOfAccountFinished.view.translations.json";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.CreationOfAccountFinished,
    "CreateAccount.View.CreationOfAccountFinished."
  )
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CreationOfAccountFinished);
