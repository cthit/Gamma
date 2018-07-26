import { connect } from "react-redux";
import _ from "lodash";
import ShowWhitelistItem from "./ShowWhitelistItem.screen";

import translations from "./ShowWhitelistItem.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.ShowWhitelistItem,
    "Whitelist.Screen.ShowWhitelistItem."
  ),
  whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowWhitelistItem);
