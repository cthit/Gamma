import { connect } from "react-redux";
import _ from "lodash";
import ShowWhitelistItem from "./ShowWhitelistItem.screen";

import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { gammaDialogOpen } from "../../../../app/views/gamma-dialog/GammaDialog.view.action-creator";
import { whitelistDelete } from "../../Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({
  whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
  redirectTo: to => dispatch(redirectTo(to)),
  toastOpen: toastData => dispatch(toastOpen(toastData)),
  gammaDialogOpen: options => dispatch(gammaDialogOpen(options)),
  whitelistDelete: whitelistId => dispatch(whitelistDelete(whitelistId))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowWhitelistItem);
