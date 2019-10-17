import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import App from "./App";

import { DigitTranslationsActions } from "@cthit/react-digit-components";

import { userUpdateMe } from "./elements/user-information/UserInformation.element.action-creator";

import { deltaLoadingStart } from "./views/delta-loading/DeltaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    loading: state.loading,
    userLoaded: state.user.loaded,
    loggedIn: state.user.loggedIn,
    fetchingAccessToken: state.deltaIntegration.fetchingAccessToken,
    errorLoadingUser: state.user.errorLoadingUser
});

const mapDispatchToProps = dispatch => ({
    userUpdateMe: () => dispatch(userUpdateMe()),
    deltaLoadingStart: () => dispatch(deltaLoadingStart()),
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
