import { connect } from "react-redux";
import ShowSuperGroupDetails from "./ShowSuperGroupDetails.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createDeleteSuperGroupAction,
    createGetSuperGroupAction,
    createGetSuperGroupSubGroupsAction
} from "../../../../api/super-groups/action-creator.super-groups.api";
import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";

const mapStateToProps = (state, ownProps) => ({
    superGroup: state.superGroups.details,
    superGroupId: ownProps.match.params.superGroupId
});

const mapDispatchToProps = dispatch => ({
    getSuperGroup: superGroupId =>
        dispatch(createGetSuperGroupAction(superGroupId)),
    getSuperGroupSubGroups: superGroupId =>
        dispatch(createGetSuperGroupSubGroupsAction(superGroupId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    deleteSuperGroup: superGroupId =>
        dispatch(createDeleteSuperGroupAction(superGroupId)),
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowSuperGroupDetails);
