import { connect } from "react-redux";
import MyAccount from "./MyAccount.view";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { DigitRedirectActions } from "@cthit/react-digit-components";

const mapStateToProps = (state, ownProps) => ({
    me: state.user
});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(MyAccount);
