import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import { createDeleteActivationCodeAction } from "../../../../api/activation-codes/action-creator.activationCodes.api";
import ShowActivationCodeDetails from "./ShowActivationCodeDetails.screen";

const mapStateToProps = (state, ownProps) => ({
    activationCode: _.find(state.activationCodes, {
        id: ownProps.match.params.id
    })
});

const mapDispatchToProps = dispatch => ({
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    deleteActivationCode: activationCodeId =>
        dispatch(createDeleteActivationCodeAction(activationCodeId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowActivationCodeDetails);
