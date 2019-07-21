import {
    DigitRedirectActions,
    DigitDialogActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import ShowClientDetails from "./ShowClientDetails.screen";
import _ from "lodash";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createGetClientAction,
    createDeleteClientAction
} from "../../../../api/clients/action-creator.clients.api";

const mapStateToProps = (state, ownProps) => ({
    client: _.find(state.clients, { id: ownProps.match.params.id }),
    clientId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    getClient: clientId => dispatch(createGetClientAction(clientId)),
    deleteClient: clientId => dispatch(createDeleteClientAction(clientId)),
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
)(ShowClientDetails);
