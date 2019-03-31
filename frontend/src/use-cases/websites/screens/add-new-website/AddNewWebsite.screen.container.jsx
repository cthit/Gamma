import { connect } from "react-redux";

import AddNewWebsite from "./AddNewWebsite.screen";

import { DigitToastActions } from "@cthit/react-digit-components";

import { createAddWebsiteAction } from "../../../../api/websites/action-creator.websites.api";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    websitesAdd: websiteData => dispatch(createAddWebsiteAction(websiteData)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewWebsite);
