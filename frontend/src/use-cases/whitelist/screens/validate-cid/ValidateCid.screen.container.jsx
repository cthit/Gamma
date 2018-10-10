import { connect } from "react-redux";
import ValidateCid from "./ValidateCid.screen";

import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";

import { whitelistValidate } from "../../Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  toastOpen: options => dispatch(toastOpen(options)),
  whitelistValidate: cid => dispatch(whitelistValidate(cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ValidateCid);
