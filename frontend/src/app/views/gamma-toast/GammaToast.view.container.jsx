import GammaToast from "./GammaToast.view";
import { connect } from "react-redux";

const mapStateToProps = state => ({
  toastOptions: state.toast
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(GammaToast);
