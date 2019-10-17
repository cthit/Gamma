import { connect } from "react-redux";

import CreationOfAccountFinished from "./CreationOfAccountFinished.view";
import { deltaLoadingFinished } from "../../../../app/views/delta-loading/DeltaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreationOfAccountFinished);
