import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import { createDeleteGroupAction } from "../../../../api/groups/action-creator.groups.api";
import ShowGroupDetails from "./ShowGroupDetails.screen";

const mapStateToProps = (state, ownProps) => ({
    group: _.find(state.groups, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    groupsDelete: groupId => dispatch(createDeleteGroupAction(groupId)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowGroupDetails);
