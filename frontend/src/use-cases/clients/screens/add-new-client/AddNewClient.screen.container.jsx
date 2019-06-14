import { connect } from "react-redux";
import AddNewClient from "./AddNewClient.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createAddClientAction } from "../../../../api/clients/action-creator.clients.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    addClient: client => dispatch(createAddClientAction(client))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewClient);
