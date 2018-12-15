import { connect } from "react-redux";
import _ from "lodash";
import EditWhitelistItemDetails from "./EditWhitelistItemDetails.screen";

import { DigitToastActions } from "@cthit/react-digit-components";

import {
    createEditWhitelistItemAction,
    createGetWhitelistItemAction
} from "../../../../api/whitelist/action-creator.whitelist.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id }),
    whitelistItemId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    whitelistChange: (whitelistItem, id) =>
        dispatch(createEditWhitelistItemAction(whitelistItem, id)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    getWhitelistItem: id => dispatch(createGetWhitelistItemAction(id)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditWhitelistItemDetails);
