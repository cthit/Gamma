import { connect } from "react-redux";
import ChangeUserPassword from "./ChangeUserPassword.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart,
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createEditUserPasswordAction, createGetUserAction } from "../../../../api/users/action-creator.users.api";
import _ from "lodash";
import {
    DigitRedirectActions,
    DigitToastActions,
} from "@cthit/react-digit-components"

const mapStateToProps = (state, ownProps) => ({
    user: _.find(state.users, { id: ownProps.match.params.id }),
    userId: ownProps.match.params.id,
});

const mapDispatchToProps = dispatch => ({
    changePassword: (userId, passwordData) =>
        dispatch(createEditUserPasswordAction(userId, passwordData)),
    getUser: userId => dispatch(createGetUserAction(userId)),
    toastOpen: toastData => dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ChangeUserPassword);