import { connect } from "react-redux";
import AddToWhitelist from "./AddToWhitelist.screen";

import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";

import { createAddToWhitelistAction } from "../../../../api/whitelist/action-creator.whitelist.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    addToWhitelist: newWhitelistAdd =>
        dispatch(createAddToWhitelistAction(newWhitelistAdd)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddToWhitelist);
