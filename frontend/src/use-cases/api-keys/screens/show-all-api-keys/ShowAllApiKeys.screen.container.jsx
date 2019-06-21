import { connect } from "react-redux";
import ShowAllApiKeys from "./ShowAllApiKeys.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createGetApiKeysAction } from "../../../../api/api-keys/action-creator.api-keys.api";

const mapStateToProps = (state, ownProps) => ({
    apiKeys: state.apiKeys,
});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    getApiKeys: () => dispatch(createGetApiKeysAction()),
});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ShowAllApiKeys);