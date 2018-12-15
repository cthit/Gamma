import { connect } from "react-redux";

import ShowAllGroups from "./ShowAllGroups.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createGetGroupsMinifiedAction } from "../../../../api/groups/action-creator.groups.api";

const mapStateToProps = (state, ownProps) => ({
    groups: state.groups
});

const mapDispatchToProps = dispatch => ({
    getGroupsMinified: () => dispatch(createGetGroupsMinifiedAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllGroups);
