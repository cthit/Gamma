import { connect } from "react-redux";
import ChangeMyPassword from "./ChangeMyPassword.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { createChangeMePasswordAction } from "../../../../../../api/me/action-creator.me.api";

const mapStateToProps = (state, ownProps) => ({
    me: state.user
});

const mapDispatchToProps = dispatch => ({
    changePassword: (userId, passwordData) =>
        dispatch(createChangeMePasswordAction(userId, passwordData)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ChangeMyPassword);
