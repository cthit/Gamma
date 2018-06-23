import { connect } from "react-redux";
import { createAccountValidateCodeAndData } from "../actions/createAccountActions";
import { toastOpen } from "../../../redux/actions/toastActions";
import InputDataAndCodeScreen from "../screens/input-data-and-code-screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = (dispatch, ownProps) => ({
  showError: textString =>
    dispatch(
      toastOpen({
        text: textString
      })
    ),
  sendDataAndCode: data => dispatch(createAccountValidateCodeAndData(data))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputDataAndCodeScreen);
