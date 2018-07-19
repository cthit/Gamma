import { connect } from "react-redux";

import App from "./App";

import { userUpdateMe } from "./elements/user-information/UserInformation.element.action-creator";

const mapStateToProps = (state, ownProps) => ({
  loaded: state.user.loaded,
  loggedIn: state.user.loggedIn
});

const mapDispatchToProps = dispatch => ({
  userUpdateMe: () => dispatch(userUpdateMe())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
