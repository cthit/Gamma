import GammaToast from "../views/gamma-toast";
import { connect } from "react-redux";

const mapStateToProps = state => ({
  toastOptions: state.toast
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(GammaToast);
