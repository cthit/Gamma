import { connect } from "react-redux";
import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { deltaLoadingFinished } from "../../../../app/views/delta-loading/DeltaLoading.view.action-creator";
import ResetPasswordInitialize from "./ResetPasswordInitalize.screen";
import { resetPasswordInitalize } from "../../ResetPassword.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    sendPasswordReset: cid => dispatch(resetPasswordInitalize(cid)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    toastOpen: data => dispatch(DigitToastActions.digitToastOpen(data))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ResetPasswordInitialize);
