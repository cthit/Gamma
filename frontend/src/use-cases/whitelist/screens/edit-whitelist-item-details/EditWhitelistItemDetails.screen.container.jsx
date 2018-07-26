import { connect } from "react-redux";
import _ from "lodash";
import EditWhitelistItemDetails from "./EditWhitelistItemDetails.screen";

import translations from "./EditWhitelistItemDetails.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import { whitelistChange } from "../../Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.EditWhitelistItemDetails,
    "Whitelist.Screen.EditWhitelistItemDetails."
  ),
  whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
  whitelistChange: (whitelistItem, id) =>
    dispatch(whitelistChange(whitelistItem, id))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditWhitelistItemDetails);
