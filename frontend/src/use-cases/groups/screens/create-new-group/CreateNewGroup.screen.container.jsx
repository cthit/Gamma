import { connect } from "react-redux";
import CreateNewGroup from "./CreateNewGroup.screen";

import { createAddGroupAction } from "../../../../api/groups/action-creator.groups.api";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    groupsAdd: group => dispatch(createAddGroupAction(group)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateNewGroup);
