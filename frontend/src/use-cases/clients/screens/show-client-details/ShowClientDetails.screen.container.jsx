import { connect } from "react-redux";
import ShowClientDetails from "./ShowClientDetails.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    create, createGetClientAction
} from "../../../../api/clients/action-creator.clients.api";

const mapStateToProps = (state, ownProps) => ({
    client: state.clients.details
});

const mapDispatchToProps = dispatch => ({
    getClient: clientId =>
        dispatch(createGetClientAction(clientId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowClientDetails);