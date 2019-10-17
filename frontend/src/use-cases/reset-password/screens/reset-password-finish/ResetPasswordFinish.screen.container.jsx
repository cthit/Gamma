import { connect } from "react-redux";
import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import {
    deltaLoadingFinished,
    deltaLoadingStart
} from "../../../../app/views/delta-loading/DeltaLoading.view.action-creator";
import ResetPasswordFinish from "./ResetPasswordFinish.screen";
import { resetPasswordFinish } from "../../ResetPassword.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    toastOpen: data => dispatch(DigitToastActions.digitToastOpen(data)),
    sendPasswordResetFinish: resetRequest =>
        dispatch(resetPasswordFinish(resetRequest)),
    deltaLoadingStart: () => dispatch(deltaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ResetPasswordFinish);
