import { connect } from "react-redux";

import Gdpr from "./Gdpr";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { usersLoad } from "../users/Users.action-creator";

const mapStateToProps = (state, ownProps) => ({
  users: state.users
});

const mapDispatchToProps = dispatch => ({
  gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
  usersLoad: () => dispatch(usersLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Gdpr);
