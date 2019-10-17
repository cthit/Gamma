import { connect } from "react-redux";
import ChangeMyPassword from "./ChangeMyPassword.screen";
import {
    deltaLoadingFinished,
    deltaLoadingStart
} from "../../../../../../app/views/delta-loading/DeltaLoading.view.action-creator";
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
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    deltaLoadingStart: () => dispatch(deltaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ChangeMyPassword);
