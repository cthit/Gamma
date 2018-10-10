import { connect } from "react-redux";
import _ from "lodash";

import ShowGroupDetails from "./ShowGroupDetails.screen";
import { gammaDialogOpen } from "../../../../app/views/gamma-dialog/GammaDialog.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { groupsDelete } from "../../Groups.action-creator";

const mapStateToProps = (state, ownProps) => ({
  group: _.find(state.groups, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
  gammaDialogOpen: options => dispatch(gammaDialogOpen(options)),
  groupsDelete: groupId => dispatch(groupsDelete(groupId)),
  toastOpen: toastData => dispatch(toastOpen(toastData)),
  redirectTo: to => dispatch(redirectTo(to))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowGroupDetails);
