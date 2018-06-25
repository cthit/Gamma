import { GammaRedirect } from "./GammaRedirect";
import { redirectFinished } from "./GammaRedirect.action-creator";
import { connect } from "react-redux";

const mapStateToProps = state => ({
  redirectPath: state.redirect.redirectPath
});

const mapDispatchToProps = dispatch => ({
  redirectFinished: () => dispatch(redirectFinished())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(GammaRedirect);
