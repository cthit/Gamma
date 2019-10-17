import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import {
    deltaLoadingStart,
    deltaLoadingFinished
} from "../../../../app/views/delta-loading/DeltaLoading.view.action-creator";
import { createAccountValidateCodeAndData } from "../../CreateAccount.action-creator";
import InputDataAndCode from "./InputDataAndCode.view";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = (dispatch, ownProps) => ({
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: path => dispatch(DigitRedirectActions.digitRedirectTo(path)),
    sendDataAndCode: data => dispatch(createAccountValidateCodeAndData(data)),
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    deltaLoadingStart: () => dispatch(deltaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(InputDataAndCode);
