import { connect } from "react-redux";

import App from "./App";

import { userUpdateMe } from "./elements/user-information/UserInformation.element.action-creator";

import { gammaLoadingStart } from "./views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  loading: state.loading,
  userLoaded: state.user.loaded,
  loggedIn: state.user.loggedIn
});

const mapDispatchToProps = dispatch => ({
  userUpdateMe: () => dispatch(userUpdateMe()),
  gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
