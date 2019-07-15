import { connect } from "react-redux";
import AddNewApiKey from "./AddNewApiKey.screen";
import { DigitDialogActions } from "@cthit/react-digit-components";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createAddApiKeyAction } from "../../../../api/api-keys/action-creator.api-keys.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    addApiKey: apiKey => dispatch(createAddApiKeyAction(apiKey)),
    openDialog: dialogData =>
        dispatch(DigitDialogActions.digitDialogCustomOpen(dialogData)),
    closeDialog: () => dispatch(DigitDialogActions.digitDialogClosedCancel())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewApiKey);
