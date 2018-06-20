import { GammaRedirect } from "../elements/gamma-redirect";
import { redirectFinished } from "../../redux/actions/redirectActions";
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
