import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import App from "./App";

import { DigitTranslationsActions } from "@cthit/react-digit-components";

import { userUpdateMe } from "./elements/user-information/UserInformation.element.action-creator";

import { gammaLoadingStart } from "./views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    loading: state.loading,
    userLoaded: state.user.loaded,
    loggedIn: state.user.loggedIn,
    fetchingAccessToken: state.gammaIntegration.fetchingAccessToken,
    errorLoadingUser: state.user.errorLoadingUser
});

const mapDispatchToProps = dispatch => ({
    userUpdateMe: () => dispatch(userUpdateMe()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
    setCommonTranslations: commonTranslations =>
        dispatch(
            DigitTranslationsActions.setCommonTranslations(commonTranslations)
        )
});

export default withRouter(
    connect(
        mapStateToProps,
        mapDispatchToProps
    )(App)
);
