import { connect } from "react-redux";

import InputCid from "./InputCid.view";
import translations from "./InputCid.view.translations.json";

import { createAccountValidateCid } from "../../CreateAccount.action-creator";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.InputCid,
    "CreateAccount.View.InputCid."
  )
});

const mapDispatchToProps = dispatch => ({
  showError: text => dispatch(toastOpen({ text: text })),
  sendCid: (cid, errorMsg) => dispatch(createAccountValidateCid(cid, errorMsg))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputCid);
