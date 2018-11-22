import {
  DigitRedirectActions,
  DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import LoginForm from "./LoginForm.view";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  toastOpen: data => dispatch(DigitToastActions.digitToastOpen(data)),
  redirectTo: path => dispatch(DigitRedirectActions.redirectTo(path))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginForm);
