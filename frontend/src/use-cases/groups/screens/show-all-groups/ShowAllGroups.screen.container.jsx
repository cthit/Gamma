import { connect } from "react-redux";

import ShowAllGroups from "./ShowAllGroups.screen";
import { createGetGroupsAction } from "../../../../api/groups/action-creator.groups.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    groups: state.groups
});

const mapDispatchToProps = dispatch => ({
    groupsLoad: () => dispatch(createGetGroupsAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllGroups);
