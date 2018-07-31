import { connect } from "react-redux";
import _ from "lodash";
import ShowUserDetails from "./ShowUserDetails.screen";

import { gammaDialogOpen } from "../../../../app/views/gamma-dialog/GammaDialog.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { usersDelete } from "../../Users.action-creator";

const mapStateToProps = (state, ownProps) => ({
  user: _.find(state.users, { cid: ownProps.match.params.cid })
});

const mapDispatchToProps = dispatch => ({
  gammaDialogOpen: options => dispatch(gammaDialogOpen(options)),
  usersDelete: cid => dispatch(usersDelete(cid)),
  toastOpen: toastData => dispatch(toastOpen(toastData)),
  redirectTo: to => dispatch(redirectTo(to))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowUserDetails);
