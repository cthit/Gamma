import { connect } from "react-redux";

import { GammaRedirect } from "./GammaRedirect.view";
import { redirectFinished } from "./GammaRedirect.view.action-creator";

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
