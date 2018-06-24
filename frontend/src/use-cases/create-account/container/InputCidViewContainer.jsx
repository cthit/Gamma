import { connect } from "react-redux";
import { createAccountValidateCid } from "../actions/createAccountActions";
import { toastOpen } from "../../../redux/actions/toastActions";
import InputCidView from "../views/input-cid-view";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  showError: text => dispatch(toastOpen({ text: text })),
  sendCid: cid => dispatch(createAccountValidateCid(cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputCidView);
