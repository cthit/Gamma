import { connect } from "react-redux";
import _ from "lodash";
import EditUserDetails from "./EditUserDetails.screen";
import { createGetWebsitesAction } from "../../../../api/websites/action-creator.websites.api";
import {
    createEditUserAction,
    createGetUserAction
} from "../../../../api/users/action-creator.users.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    DigitToastActions,
    DigitRedirectActions
} from "@cthit/react-digit-components";

const mapStateToProps = (state, ownProps) => ({
    user: _.find(state.users, { id: ownProps.match.params.id }),
    userId: ownProps.match.params.id,
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({
    usersChange: (user, id) => dispatch(createEditUserAction(user, id)),
    websitesLoad: () => dispatch(createGetWebsitesAction()),
    getUser: id => dispatch(createGetUserAction(id)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUserDetails);
