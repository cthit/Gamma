import { connect } from "react-redux";

import InputDataAndCode from "./InputDataAndCode.view";
import translations from "./InputDataAndCode.view.translations.json";

import { createAccountValidateCodeAndData } from "../../CreateAccount.action-creator";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";

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
  sendDataAndCode: (data, errorMessages) =>
    dispatch(createAccountValidateCodeAndData(data, errorMessages))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputDataAndCode);
