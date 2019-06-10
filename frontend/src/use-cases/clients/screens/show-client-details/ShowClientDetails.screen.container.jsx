import { connect } from "react-redux";
import ShowClientDetails from "./ShowClientDetails.screen";
import _ from "lodash";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    createGetClientAction
} from "../../../../api/clients/action-creator.clients.api";

const mapStateToProps = (state, ownProps) => ({
    client: _.find(state.clients, {id: ownProps.match.params.id}),
    s: state,
    own: ownProps
});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished : () => dispatch(gammaLoadingFinished()),
    getClient: clientId => dispatch(createGetClientAction(clientId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowClientDetails);