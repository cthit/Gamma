import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import {
    deltaLoadingStart,
    deltaLoadingFinished
} from "../../../../app/views/delta-loading/DeltaLoading.view.action-creator";
import { connect } from "react-redux";
import { createAccountValidateCid } from "../../CreateAccount.action-creator";
import InputCid from "./InputCid.view";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    toastOpen: data => dispatch(DigitToastActions.digitToastOpen(data)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    sendCid: cid => dispatch(createAccountValidateCid(cid)),
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    deltaLoadingStart: () => dispatch(deltaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(InputCid);
