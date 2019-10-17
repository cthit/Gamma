import { connect } from "react-redux";
import MyAccount from "./MyAccount.view";
import { deltaLoadingFinished } from "../../../../app/views/delta-loading/DeltaLoading.view.action-creator";
import { DigitRedirectActions } from "@cthit/react-digit-components";

const mapStateToProps = (state, ownProps) => ({
    me: state.user
});

const mapDispatchToProps = dispatch => ({
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished()),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(MyAccount);
