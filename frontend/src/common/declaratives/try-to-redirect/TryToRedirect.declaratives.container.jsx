import { connect } from "react-redux";
import { redirectTo } from "../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import TryToRedirect from "./TryToRedirect.declaratives";

const mapStateToProps = (state, ownProps) => ({
  redirectFinished: state.redirect.redirectPath == null
});

const mapDispatchToProps = dispatch => ({
  redirectTo: to => dispatch(redirectTo(to))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TryToRedirect);
