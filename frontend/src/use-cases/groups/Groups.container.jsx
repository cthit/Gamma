import { connect } from "react-redux";

import { groupsLoad } from "./Groups.action-creator";

import Groups from "./Groups";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  groupsLoad: () => dispatch(groupsLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Groups);
