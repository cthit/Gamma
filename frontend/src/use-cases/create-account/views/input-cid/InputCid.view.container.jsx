import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { createAccountValidateCid } from "../../CreateAccount.action-creator";
import InputCid from "./InputCid.view";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    toastOpen: data => dispatch(DigitToastActions.digitToastOpen(data)),
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    sendCid: cid => dispatch(createAccountValidateCid(cid))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(InputCid);
