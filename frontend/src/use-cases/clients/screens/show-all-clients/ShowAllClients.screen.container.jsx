import { connect } from "react-redux";
import ShowAllClients from "./ShowAllClients.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createGetClientsAction } from "../../../../api/clients/action-creator.clients.api";

const mapStateToProps = (state, ownProps) => ({
    clients: state.clients,
});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    getClients: () => dispatch(createGetClientsAction())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllClients);
