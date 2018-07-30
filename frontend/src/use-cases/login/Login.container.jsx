import { connect } from "react-redux";
import Login from "./Login";

import { login } from "./Login.action-creator";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  redirectTo: path => dispatch(redirectTo(path)),
  login: (data, persistant) => dispatch(login(data, persistant))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);
