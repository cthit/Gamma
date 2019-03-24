import { connect } from "react-redux";
import ShowAllSuperGroups from "./ShowAllSuperGroups.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createLoadSuperGroupsAction } from "../../../../api/super-groups/action-creator.super-groups.api";

const mapStateToProps = (state, ownProps) => ({
    superGroups: state.superGroups.all
});

const mapDispatchToProps = dispatch => ({
    getSuperGroups: () => dispatch(createLoadSuperGroupsAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllSuperGroups);
