import { connect } from "react-redux";

import Websites from "./Websites";

import { createGetWebsitesAction } from "../../api/websites/action-creator.websites.api";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    websitesLoad: () => dispatch(createGetWebsitesAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Websites);
