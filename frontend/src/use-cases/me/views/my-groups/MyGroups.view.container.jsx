import { connect } from "react-redux";
import MyGroups from "./MyGroups.view";
import { deltaLoadingFinished } from "../../../../app/views/delta-loading/DeltaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    me: state.user
});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(MyGroups);
