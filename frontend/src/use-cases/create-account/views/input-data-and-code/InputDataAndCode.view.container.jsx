import { connect } from "react-redux";

import InputDataAndCode from "./InputDataAndCode.view";

import { createAccountValidateCodeAndData } from "../../CreateAccount.action-creator";

import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = (dispatch, ownProps) => ({
  toastOpen: toastData => dispatch(toastOpen(toastData)),
  redirectTo: path => dispatch(redirectTo(path)),
  sendDataAndCode: data => dispatch(createAccountValidateCodeAndData(data))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputDataAndCode);
