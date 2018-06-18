import { connect } from "react-redux";
import { createAccountValidateCid } from "../actions/createAccountActions";
import InputCidScreen from "../screens/input-cid-screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = (dispatch, ownProps) => ({
  sendCid: cid => dispatch(createAccountValidateCid(cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InputCidScreen);
