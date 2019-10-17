import { connect } from "react-redux";

import { deltaLoadingFinished } from "../../app/views/delta-loading/DeltaLoading.view.action-creator";

import ResetPassword from "./ResetPassword";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ResetPassword);
