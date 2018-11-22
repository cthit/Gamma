import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import { whitelistDelete } from "../../Whitelist.action-creator";
import ShowWhitelistItem from "./ShowWhitelistItem.screen";

const mapStateToProps = (state, ownProps) => ({
    whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    whitelistDelete: whitelistId => dispatch(whitelistDelete(whitelistId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowWhitelistItem);
