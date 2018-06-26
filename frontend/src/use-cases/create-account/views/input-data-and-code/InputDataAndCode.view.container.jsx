import InputDataAndCode from "./InputDataAndCode.view";
import { connect } from "react-redux";
import { createAccountValidateCodeAndData } from "../../CreateAccount.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";
import translations from "./InputDataAndCode.view.translations.json";

const mapStateToProps = (state, ownProps) => {
  return {
    text: loadTranslations(
      state.localize,
      translations.InputDataAndCode,
      "CreateAccount.View.InputDataAndCode."
    )
  };
};

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
)(InputDataAndCode);
