import { connect } from "react-redux";

import { createGetGroupsAction } from "../../api/groups/action-creator.groups.api";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

import Groups from "./Groups";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    groupsLoad: () => dispatch(createGetGroupsAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Groups);
