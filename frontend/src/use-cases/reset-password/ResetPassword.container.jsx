import { connect } from "react-redux";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

import ResetPassword from "./ResetPassword";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ResetPassword);
