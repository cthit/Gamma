import { connect } from "react-redux";
import {
    DigitRedirectActions,
    DigitToastActions,
} from "@cthit/react-digit-components";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import ResetPasswordFinish from "./ResetPasswordFinish.screen";
import { resetPasswordFinish } from "../../ResetPassword.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    toastOpen: data => dispatch(DigitToastActions.digitToastOpen(data)),
    sendPasswordResetFinish: resetRequest => dispatch(resetPasswordFinish(resetRequest))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ResetPasswordFinish);