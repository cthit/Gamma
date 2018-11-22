import { connect } from "react-redux";
import Whitelist from "./Whitelist";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

import { whitelistLoad } from "./Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    whitelistLoad: () => dispatch(whitelistLoad()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Whitelist);
