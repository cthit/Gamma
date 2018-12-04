import { connect } from "react-redux";
import Whitelist from "./Whitelist";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

import { createGetWhitelistAction } from "../../api/whitelist/action-creator.whitelist.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    whitelistLoad: () => dispatch(createGetWhitelistAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Whitelist);
