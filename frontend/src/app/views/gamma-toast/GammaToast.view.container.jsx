import { connect } from "react-redux";

import GammaToast from "./GammaToast.view";

const mapStateToProps = state => ({
  toastOptions: state.toast
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(GammaToast);
