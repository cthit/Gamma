import { DigitToastActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { createValidateWhitelistAction } from "../../../../api/whitelist/action-creator.whitelist.api";
import ValidateCid from "./ValidateCid.screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    toastOpen: options => dispatch(DigitToastActions.digitToastOpen(options)),
    whitelistValidate: cid => dispatch(createValidateWhitelistAction(cid))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ValidateCid);
