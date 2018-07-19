import { connect } from "react-redux";

import App from "./App";

import { userUpdateMe } from "./elements/user-information/UserInformation.element.action-creator";
import loadTranslations from "../common/utils/loaders/translations.loader";
import appTranslations from "./App.translations.json";

const mapStateToProps = (state, ownProps) => ({
  loaded: state.user.loaded,
  loggedIn: state.user.loggedIn,
  text: loadTranslations(state.localize, appTranslations.App, "App.")
});

const mapDispatchToProps = dispatch => ({
  userUpdateMe: () => dispatch(userUpdateMe())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
