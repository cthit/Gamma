import { connect } from "react-redux";
import Login from "./Login";

import { login } from "./Login.action-creator";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  redirectTo: path => dispatch(redirectTo(path)),
  login: (data, persistant) => dispatch(login(data, persistant)),
  gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);
