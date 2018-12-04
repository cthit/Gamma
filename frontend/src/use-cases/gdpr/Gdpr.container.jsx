import { connect } from "react-redux";

import Gdpr from "./Gdpr";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createGetUsersAction } from "../../api/users/action-creator.users.api";

const mapStateToProps = (state, ownProps) => ({
    users: state.users
});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    usersLoad: () => dispatch(createGetUsersAction())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Gdpr);
