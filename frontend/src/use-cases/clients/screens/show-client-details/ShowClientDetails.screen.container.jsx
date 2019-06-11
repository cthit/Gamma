import { connect } from "react-redux";
import ShowClientDetails from "./ShowClientDetails.screen";
import _ from "lodash";
import { gammaLoadingFinished, gammaLoadingStart } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createGetClientAction,
} from "../../../../api/clients/action-creator.clients.api";

const mapStateToProps = (state, ownProps) => ({
    client: _.find(state.clients, {id: ownProps.match.params.id}),
    clientId: ownProps.match.params.id,
});

const mapDispatchToProps = dispatch => ({
    getClient: clientId => dispatch(createGetClientAction(clientId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowClientDetails);