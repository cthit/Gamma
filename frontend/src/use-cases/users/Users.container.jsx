import { connect } from "react-redux";

import { createGetUsersAction } from "../../api/users/action-creator.users.api";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

import Users from "./Users";

const mapStateToProps = (state, ownProps) => ({
    users: state.users
});

const mapDispatchToProps = dispatch => ({
    usersLoad: () => dispatch(createGetUsersAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Users);
