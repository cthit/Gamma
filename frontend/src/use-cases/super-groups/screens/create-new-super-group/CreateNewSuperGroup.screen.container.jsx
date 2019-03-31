import { connect } from "react-redux";

import CreateNewSuperGroup from "./CreateNewSuperGroup.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createAddSuperGroupAction } from "../../../../api/super-groups/action-creator.super-groups.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    addSuperGroup: superGroupData =>
        dispatch(createAddSuperGroupAction(superGroupData)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateNewSuperGroup);
