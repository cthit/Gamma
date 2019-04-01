import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import {
    createDeleteGroupAction,
    createGetGroupAction
} from "../../../../api/groups/action-creator.groups.api";
import ShowGroupDetails from "./ShowGroupDetails.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    group: state.groups != null ? state.groups.details : null,
    groupId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    groupsDelete: groupId => dispatch(createDeleteGroupAction(groupId)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    getGroup: groupId => dispatch(createGetGroupAction(groupId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowGroupDetails);
