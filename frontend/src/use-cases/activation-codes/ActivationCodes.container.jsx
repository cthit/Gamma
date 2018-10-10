import { connect } from "react-redux";

import ActivationCodes from "./ActivationCodes";

import { activationCodesLoad } from "./ActivationCodes.action-creator";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  activationCodesLoad: () => dispatch(activationCodesLoad()),
  gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ActivationCodes);
