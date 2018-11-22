import { DigitRedirectActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import CreateAccount from "./CreateAccount";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    redirectTo: path => dispatch(DigitRedirectActions.redirectTo(path)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateAccount);
