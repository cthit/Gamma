import { connect } from "react-redux";

import Gdpr from "./Gdpr";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createGetGDPRMinifiedRequest,
    createSetGDPRRequest
} from "../../api/gdpr/action-creator.gdpr.api";

const mapStateToProps = (state, ownProps) => ({
    users: state.gdpr
});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    getUsersWithGDPR: () => dispatch(createGetGDPRMinifiedRequest()),
    setGDPRValue: (userId, data) => dispatch(createSetGDPRRequest(userId, data))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Gdpr);
