import { connect } from "react-redux";
import { createAccountValidateCid } from "../../CreateAccount.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import InputCid from "./InputCid.view";

import loadTranslations from "../../../../common/utils/loadTranslations";
import translations from "./InputCid.view.translations.json";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.InputCid,
    "CreateAccount.View.InputCid."
  )
});

const mapDispatchToProps = dispatch => ({
  showError: text => dispatch(toastOpen({ text: text })),
  sendCid: cid => dispatch(createAccountValidateCid(cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputCid);
