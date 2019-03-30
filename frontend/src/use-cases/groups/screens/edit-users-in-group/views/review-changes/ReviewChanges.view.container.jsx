import { connect } from "react-redux";
import ReviewChanges from "./ReviewChanges.view";
import { DigitRedirectActions } from "@cthit/react-digit-components";
import { createAddUserToGroupRequest } from "../../../../../../api/groups/action-creator.groups.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    addUserToGroup: (groupId, memberData) =>
        dispatch(createAddUserToGroupRequest(groupId, memberData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ReviewChanges);
