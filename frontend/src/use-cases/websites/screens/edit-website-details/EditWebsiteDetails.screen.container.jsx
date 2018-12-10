import { connect } from "react-redux";
import _ from "lodash";
import EditWebsiteDetails from "./EditWebsiteDetails.screen";

import { DigitToastActions } from "@cthit/react-digit-components";

import {
    createEditWebsiteAction,
    createGetWebsiteAction
} from "../../../../api/websites/action-creator.websites.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    website: _.find(state.websites, { id: ownProps.match.params.id }),
    websiteId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    websitesChange: (websiteData, websiteId) =>
        dispatch(createEditWebsiteAction(websiteData, websiteId)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    getWebsite: websiteId => dispatch(createGetWebsiteAction(websiteId)),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditWebsiteDetails);
