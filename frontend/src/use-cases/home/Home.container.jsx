import { connect } from "react-redux";

import Home from "./Home";

import { deltaLoadingFinished } from "../../app/views/delta-loading/DeltaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    user: state.user
});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Home);
