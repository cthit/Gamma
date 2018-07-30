import { connect } from "react-redux";
import _ from "lodash";
import EditWhitelistItemDetails from "./EditWhitelistItemDetails.screen";

import { whitelistChange } from "../../Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({
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
