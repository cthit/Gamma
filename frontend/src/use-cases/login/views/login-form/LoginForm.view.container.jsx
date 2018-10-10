import { connect } from "react-redux";

import LoginForm from "./LoginForm.view";

import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  toastOpen: data => dispatch(toastOpen(data)),
  redirectTo: path => dispatch(redirectTo(path))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginForm);
