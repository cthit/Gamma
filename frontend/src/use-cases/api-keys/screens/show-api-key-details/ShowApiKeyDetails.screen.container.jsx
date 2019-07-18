import {
    DigitRedirectActions,
    DigitDialogActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import ShowApiKeyDetails from "./ShowApiKeyDetails.screen";
import _ from "lodash";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createGetApiKeyAction,
    createDeleteApiKeyAction
} from "../../../../api/api-keys/action-creator.api-keys.api";
const mapStateToProps = (state, ownProps) => ({
    apiKey: _.find(state.apiKeys, { id: ownProps.match.params.id }),
    apiKeyId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    getApiKey: apiKeyId => dispatch(createGetApiKeyAction(apiKeyId)),
    deleteApiKey: apiKeyId => dispatch(createDeleteApiKeyAction(apiKeyId)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowApiKeyDetails);
