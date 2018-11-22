import {
  DigitDialogActions,
  DigitRedirectActions,
  DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import { groupsDelete } from "../../Groups.action-creator";
import ShowGroupDetails from "./ShowGroupDetails.screen";

const mapStateToProps = (state, ownProps) => ({
  group: _.find(state.groups, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
  dialogOpen: options => dispatch(DigitDialogActions.digitDialogOpen(options)),
  groupsDelete: groupId => dispatch(groupsDelete(groupId)),
  toastOpen: toastData => dispatch(DigitToastActions.digitToastOpen(toastData)),
  redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowGroupDetails);
