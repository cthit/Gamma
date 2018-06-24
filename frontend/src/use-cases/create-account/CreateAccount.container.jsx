import { connect } from "react-redux";
import { redirectTo } from "../../redux/actions/redirectActions";
import CreateAccount from "./CreateAccount";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  redirectTo: path => dispatch(redirectTo(path))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CreateAccount);
