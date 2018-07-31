import { connect } from "react-redux";
import _ from "lodash";
import ShowUserDetails from "./ShowUserDetails.screen";

import { gammaDialogOpen } from "../../../../app/views/gamma-dialog/GammaDialog.view.action-creator";
import { usersDelete } from "../../Users.action-creator";

const mapStateToProps = (state, ownProps) => ({
  user: _.find(state.users, { cid: ownProps.match.params.cid })
});

const mapDispatchToProps = dispatch => ({
  gammaDialogOpen: options => dispatch(gammaDialogOpen(options)),
  usersDelete: cid => dispatch(usersDelete(cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowUserDetails);
