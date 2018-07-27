import { connect } from "react-redux";

import ShowAllGroups from "./ShowAllGroups.screen";

const mapStateToProps = (state, ownProps) => ({
  groups: state.groups
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowAllGroups);
