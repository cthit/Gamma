import { connect } from "react-redux";
import ShowAllUsers from "./ShowAllUsers.screen";
import { createGetUsersAction } from "../../../../api/users/action-creator.users.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    users: state.users
});

const mapDispatchToProps = dispatch => ({
    getUsers: () => dispatch(createGetUsersAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllUsers);
