import { connect } from "react-redux";

import EditSuperGroupDetails from "./EditSuperGroupDetails.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createEditSuperGroupAction,
    createGetSuperGroupAction
} from "../../../../api/super-groups/action-creator.super-groups.api";
import { DigitToastActions } from "@cthit/react-digit-components";

const mapStateToProps = (state, ownProps) => ({
    superGroup:
        state.superGroups.details != null
            ? state.superGroups.details.data
            : null,
    superGroupId: ownProps.match.params.superGroupId
});

const mapDispatchToProps = dispatch => ({
    editSuperGroup: (superGroupId, superGroupData) =>
        dispatch(createEditSuperGroupAction(superGroupId, superGroupData)),
    getSuperGroup: superGroupId =>
        dispatch(createGetSuperGroupAction(superGroupId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditSuperGroupDetails);
