import { connect } from "react-redux";
import ShowAllSuperGroups from "./ShowAllSuperGroups.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllSuperGroups);
