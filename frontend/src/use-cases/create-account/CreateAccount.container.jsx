import { DigitRedirectActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { deltaLoadingFinished } from "../../app/views/delta-loading/DeltaLoading.view.action-creator";
import CreateAccount from "./CreateAccount";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    redirectTo: path => dispatch(DigitRedirectActions.digitRedirectTo(path)),
    deltaLoadingFinished: () => dispatch(deltaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateAccount);
