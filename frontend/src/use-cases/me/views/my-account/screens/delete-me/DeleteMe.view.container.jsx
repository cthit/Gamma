import { connect } from "react-redux";
import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import DeleteMe from "./DeleteMe.view";
import { createDeleteMeAction } from "../../../../../../api/me/action-creator.me.api";
import { deltaLoadingFinished } from "../../../../../../app/views/delta-loading/DeltaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    me: state.user
});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    deleteMe: passwordData => dispatch(createDeleteMeAction(passwordData)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(DeleteMe);
