import { DigitRedirectActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import Login from "./Login";
import { login } from "./Login.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    redirectTo: path => dispatch(DigitRedirectActions.redirectTo(path)),
    login: (data, persistant) => dispatch(login(data, persistant)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Login);
