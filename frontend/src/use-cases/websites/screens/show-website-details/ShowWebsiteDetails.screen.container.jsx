import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import {
    createDeleteWebsiteAction,
    createGetWebsiteAction
} from "../../../../api/websites/action-creator.websites.api";
import ShowWebsiteDetails from "./ShowWebsiteDetails.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    website: _.find(state.websites, { id: ownProps.match.params.id }),
    websiteId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    websitesDelete: websiteId => dispatch(createDeleteWebsiteAction(websiteId)),
    getWebsite: websiteId => dispatch(createGetWebsiteAction(websiteId)),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowWebsiteDetails);
