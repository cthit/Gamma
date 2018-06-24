import { connect } from "react-redux";
import { createAccountValidateCodeAndData } from "../actions/createAccountActions";
import { toastOpen } from "../../../redux/actions/toastActions";
import { redirectTo } from "../../../redux/actions/redirectActions";
import InputDataAndCodeView from "../views/input-data-and-code-view";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = (dispatch, ownProps) => ({
  showError: textString =>
    dispatch(
      toastOpen({
        text: textString
      })
    ),
  redirectTo: path => dispatch(redirectTo(path)),
  sendDataAndCode: data => dispatch(createAccountValidateCodeAndData(data))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputDataAndCodeView);
