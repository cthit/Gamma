import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import {
    createDeleteUserAction,
    createGetUserAction
} from "../../../../api/users/action-creator.users.api";
import ShowUserDetails from "./ShowUserDetails.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    user: _.find(state.users, { cid: ownProps.match.params.cid }),
    userCid: ownProps.match.params.cid
});

const mapDispatchToProps = dispatch => ({
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    usersDelete: cid => dispatch(createDeleteUserAction(cid)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    getUser: userCid => dispatch(createGetUserAction(userCid)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowUserDetails);
