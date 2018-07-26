import { connect } from "react-redux";
import AddNewWhitelistItem from "./AddNewWhitelistItem.screen";

import translations from "./AddNewWhitelistItem.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import { whitelistAdd } from "../../Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.AddNewWhitelistItem,
    "Whitelist.Screen.AddNewWhitelistItem."
  )
});

const mapDispatchToProps = dispatch => ({
  whitelistAdd: newWhitelistAdd => dispatch(whitelistAdd(newWhitelistAdd))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddNewWhitelistItem);
