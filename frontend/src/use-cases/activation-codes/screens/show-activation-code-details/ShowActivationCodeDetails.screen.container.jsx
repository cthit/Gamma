import { connect } from "react-redux";
import _ from "lodash";

import ShowActivationCodeDetails from "./ShowActivationCodeDetails.screen";

const mapStateToProps = (state, ownProps) => ({
  activationCode: _.find(state.activationCodes, {
    id: ownProps.match.params.id
  })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowActivationCodeDetails);
