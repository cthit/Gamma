import { connect } from "react-redux";
import _ from "lodash";
import EditWhitelistItemDetails from "./EditWhitelistItemDetails.screen";

import { DigitToastActions } from "@cthit/react-digit-components";

import { createEditWhitelistItemAction } from "../../../../api/whitelist/action-creator.whitelist.api";

const mapStateToProps = (state, ownProps) => ({
    whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    whitelistChange: (whitelistItem, id) =>
        dispatch(createEditWhitelistItemAction(whitelistItem, id)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditWhitelistItemDetails);
