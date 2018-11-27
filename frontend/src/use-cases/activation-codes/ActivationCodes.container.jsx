import { connect } from "react-redux";

import ActivationCodes from "./ActivationCodes";

import { createGetActivationCodesAction } from "../../api/activation-codes/action-creator.activationCodes.api";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    getActivationCodes: () => dispatch(createGetActivationCodesAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ActivationCodes);
