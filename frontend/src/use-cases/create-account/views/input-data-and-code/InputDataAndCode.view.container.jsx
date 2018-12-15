import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import {
    gammaLoadingStart,
    gammaLoadingFinished
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createAccountValidateCodeAndData } from "../../CreateAccount.action-creator";
import InputDataAndCode from "./InputDataAndCode.view";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = (dispatch, ownProps) => ({
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: path => dispatch(DigitRedirectActions.redirectTo(path)),
    sendDataAndCode: data => dispatch(createAccountValidateCodeAndData(data)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(InputDataAndCode);
