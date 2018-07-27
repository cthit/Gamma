import { connect } from "react-redux";
import _ from "lodash";

import ShowGroupDetails from "./ShowGroupDetails.screen";

const mapStateToProps = (state, ownProps) => ({
  group: _.find(state.groups, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowGroupDetails);
