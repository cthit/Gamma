import { connect } from "react-redux";
import CreateNewGroup from "./CreateNewGroup.screen";

import { createAddGroupAction } from "../../../../api/groups/action-creator.groups.api";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createLoadSuperGroupsAction } from "../../../../api/super-groups/action-creator.super-groups.api";

const mapStateToProps = (state, ownProps) => ({
    superGroups: state.superGroups.all
});

const mapDispatchToProps = dispatch => ({
    groupsAdd: group => dispatch(createAddGroupAction(group)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    getSuperGroups: () => dispatch(createLoadSuperGroupsAction())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateNewGroup);
