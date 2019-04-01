import { DigitToastActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import UserInformation from "./UserInformation.element";

const mapStateToProps = (state, ownProps) => ({
    user: state.user,
    loggedIn: state.user.loggedIn,
    loaded: state.user.loaded
});

const mapDispatchToProps = dispatch => ({
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(UserInformation);
