import { connect } from "react-redux";
import { createGetWebsitesAction } from "../../../../../../api/websites/action-creator.websites.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    DigitToastActions,
    DigitRedirectActions
} from "@cthit/react-digit-components";
import EditMe from "./EditMe.screen";
import { createEditMeAction } from "../../../../../../api/me/action-creator.me.api";

const mapStateToProps = (state, ownProps) => ({
    me: state.user,
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({
    editMe: userData => dispatch(createEditMeAction(userData)),
    websitesLoad: () => dispatch(createGetWebsitesAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditMe);
