import { connect } from "react-redux";

import ShowAllActivationCodes from "./ShowAllActivationCodes.screen";

const mapStateToProps = (state, ownProps) => ({
    activationCodes: state.activationCodes
});

const mapDispatchToProps = dispatch => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllActivationCodes);
