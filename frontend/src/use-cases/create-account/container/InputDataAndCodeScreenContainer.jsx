import { connect } from "react-redux";
import { createAccountValidateCodeAndData } from "../actions/createAccountActions";
import InputDataAndCodeScreen from "../screens/input-data-and-code-screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = (dispatch, ownProps) => ({
  sendDataAndCode: (code, user) =>
    dispatch(createAccountValidateCodeAndData(code, user))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputDataAndCodeScreen);
