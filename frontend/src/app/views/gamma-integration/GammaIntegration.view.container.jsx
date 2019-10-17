import { connect } from "react-redux";

import { DigitRedirectActions } from "@cthit/react-digit-components";

import DeltaIntegration from "./DeltaIntegration.view";

import {
    startedFetchingAccessToken,
    finishedFetchingAccessToken
} from "./DeltaIntegration.view.action-creator";
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
)(DeltaIntegration);
