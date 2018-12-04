import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import { createDeleteUserAction } from "../../../../api/users/action-creator.users.api";
import ShowUserDetails from "./ShowUserDetails.screen";

const mapStateToProps = (state, ownProps) => ({
    user: _.find(state.users, { cid: ownProps.match.params.cid })
});

const mapDispatchToProps = dispatch => ({
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    usersDelete: cid => dispatch(createDeleteUserAction(cid)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowUserDetails);
