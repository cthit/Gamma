import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createDeleteWhitelistItemAction,
    createGetWhitelistItemAction
} from "../../../../api/whitelist/action-creator.whitelist.api";
import ShowWhitelistItem from "./ShowWhitelistItem.screen";

const mapStateToProps = (state, ownProps) => ({
    whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id }),
    whitelistItemId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    getWhitelistItem: id => dispatch(createGetWhitelistItemAction(id)),
    whitelistDelete: whitelistId =>
        dispatch(createDeleteWhitelistItemAction(whitelistId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowWhitelistItem);
