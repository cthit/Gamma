import { connect } from "react-redux";

import InputCid from "./InputCid.view";
import translations from "./InputCid.view.translations.json";

import { createAccountValidateCid } from "../../CreateAccount.action-creator";

import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  toastOpen: data => dispatch(toastOpen(data)),
  redirectTo: to => dispatch(redirectTo(to)),
  sendCid: cid => dispatch(createAccountValidateCid(cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputCid);
