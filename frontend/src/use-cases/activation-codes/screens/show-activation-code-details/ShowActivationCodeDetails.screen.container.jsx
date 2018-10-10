import { connect } from "react-redux";
import _ from "lodash";

import ShowActivationCodeDetails from "./ShowActivationCodeDetails.screen";

import { gammaDialogOpen } from "../../../../app/views/gamma-dialog/GammaDialog.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { activationCodesDelete } from "../../ActivationCodes.action-creator";

const mapStateToProps = (state, ownProps) => ({
  activationCode: _.find(state.activationCodes, {
    id: ownProps.match.params.id
  })
});

const mapDispatchToProps = dispatch => ({
  gammaDialogOpen: options => dispatch(gammaDialogOpen(options)),
  toastOpen: toastData => dispatch(toastOpen(toastData)),
  redirectTo: to => dispatch(redirectTo(to)),
  activationCodesDelete: activationCodeId =>
    dispatch(activationCodesDelete(activationCodeId))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowActivationCodeDetails);
