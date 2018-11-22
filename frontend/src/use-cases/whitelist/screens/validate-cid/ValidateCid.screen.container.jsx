import { DigitToastActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { whitelistValidate } from "../../Whitelist.action-creator";
import ValidateCid from "./ValidateCid.screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  toastOpen: options => dispatch(DigitToastActions.digitToastOpen(options)),
  whitelistValidate: cid => dispatch(whitelistValidate(cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ValidateCid);
