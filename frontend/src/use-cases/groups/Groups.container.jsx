import { connect } from "react-redux";

import { groupsLoad } from "./Groups.action-creator";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

import Groups from "./Groups";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  groupsLoad: () => dispatch(groupsLoad()),
  gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Groups);
