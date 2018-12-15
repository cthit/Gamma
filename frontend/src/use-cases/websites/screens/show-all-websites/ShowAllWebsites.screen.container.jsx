import { connect } from "react-redux";

import ShowAllWebsites from "./ShowAllWebsites.screen";
import { createGetWebsitesAction } from "../../../../api/websites/action-creator.websites.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({
    getWebsites: () => dispatch(createGetWebsitesAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllWebsites);
