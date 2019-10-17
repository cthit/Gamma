import { connect } from "react-redux";

import Gdpr from "./Gdpr";

import { deltaLoadingFinished } from "../../app/views/delta-loading/DeltaLoading.view.action-creator";
import {
    createGetGDPRMinifiedRequest,
    createSetGDPRRequest
} from "../../api/gdpr/action-creator.gdpr.api";

const mapStateToProps = (state, ownProps) => ({
    users: state.gdpr
});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    getUsersWithGDPR: () => dispatch(createGetGDPRMinifiedRequest()),
    setGDPRValue: (userId, data) => dispatch(createSetGDPRRequest(userId, data))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Gdpr);
