import { connect } from "react-redux";

import Websites from "./Websites";

import { websitesLoad } from "./Websites.action-creator";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    websitesLoad: () => dispatch(websitesLoad()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Websites);
