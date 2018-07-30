import { connect } from "react-redux";

import ActivationCodes from "./ActivationCodes";

import { activationCodesLoad } from "./ActivationCodes.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  activationCodesLoad: () => dispatch(activationCodesLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ActivationCodes);
