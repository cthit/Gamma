import { connect } from "react-redux";

import { usersLoad } from "./Users.action-creator";

import Users from "./Users";

const mapStateToProps = (state, ownProps) => ({
  users: state.users
});

const mapDispatchToProps = dispatch => ({
  usersLoad: () => dispatch(usersLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Users);
