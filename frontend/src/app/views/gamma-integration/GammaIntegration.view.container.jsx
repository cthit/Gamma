import { connect } from "react-redux";

import { DigitRedirectActions } from "@cthit/react-digit-components";

import GammaIntegration from "./GammaIntegration.view";

import {
    startedFetchingAccessToken,
    finishedFetchingAccessToken
} from "./GammaIntegration.view.action-creator";
import { userUpdateMe } from "../../elements/user-information/UserInformation.element.action-creator";

const mapStateToProps = () => ({});

const mapDispatchToProps = dispatch => ({
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    startedFetchingAccessToken: () => dispatch(startedFetchingAccessToken()),
    finishedFetchingAccessToken: () => dispatch(finishedFetchingAccessToken()),
    userUpdateMe: () => dispatch(userUpdateMe())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(GammaIntegration);
