import {
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import { connect } from "react-redux";
import ContainUserToAllowedPages from "./ContainUserToAllowedPages.declarative";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    toastOpen: data => dispatch(DigitToastActions.digitToastOpen(data))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ContainUserToAllowedPages);
