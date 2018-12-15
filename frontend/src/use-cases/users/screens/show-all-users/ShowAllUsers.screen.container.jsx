import { connect } from "react-redux";
import ShowAllUsers from "./ShowAllUsers.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createGetUsersMinifiedAction } from "../../../../api/users/action-creator.users.api";

const mapStateToProps = (state, ownProps) => ({
    users: state.users
});

const mapDispatchToProps = dispatch => ({
    getUsersMinified: () => dispatch(createGetUsersMinifiedAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllUsers);
