import { connect } from "react-redux";
import { deltaLoadingFinished } from "../../app/views/delta-loading/DeltaLoading.view.action-creator";
import FourOFour from "./FourOFour";

const mapStateToProps = (state, ownProps) => ({});
const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(FourOFour);
