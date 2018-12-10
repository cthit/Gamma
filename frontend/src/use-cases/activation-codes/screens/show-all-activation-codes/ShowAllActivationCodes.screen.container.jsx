import { connect } from "react-redux";

import ShowAllActivationCodes from "./ShowAllActivationCodes.screen";
import { createGetActivationCodesAction } from "../../../../api/activation-codes/action-creator.activationCodes.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    activationCodes: state.activationCodes
});

const mapDispatchToProps = dispatch => ({
    getActivationCodes: () => dispatch(createGetActivationCodesAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllActivationCodes);
