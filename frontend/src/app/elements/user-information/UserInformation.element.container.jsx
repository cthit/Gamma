import { connect } from "react-redux";
import UserInformation from "./UserInformation.element";

import { userLogout } from "./UserInformation.element.action-creator";

import loadTranslations from "../../../common/utils/loaders/translations.loader";
import { toastOpen } from "../../views/gamma-toast/GammaToast.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
  user: state.user,
  text: loadTranslations(state.localize),
  loggedIn: state.user.loggedIn,
  loaded: state.user.loaded
});

const mapDispatchToProps = dispatch => ({
  logout: loggedOutText => dispatch(userLogout(loggedOutText)),
  toastOpen: toastData => dispatch(toastOpen(toastData))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(UserInformation);
