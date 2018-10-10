import { connect } from "react-redux";
import CreateAccount from "./CreateAccount";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  redirectTo: path => dispatch(redirectTo(path)),
  gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CreateAccount);
