import { connect } from "react-redux";

import GammaDialog from "./GammaDialog.view";

import {
  gammaDialogClosedCancel,
  gammaDialogClosedConfirm
} from "./GammaDialog.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  options: state.dialog
});

const mapDispatchToProps = dispatch => ({
  gammaDialogClosedConfirm: () => dispatch(gammaDialogClosedConfirm()),
  gammaDialogClosedCancel: () => dispatch(gammaDialogClosedCancel())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(GammaDialog);
