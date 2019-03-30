import { connect } from "react-redux";
import SelectMembers from "./SelectMembers.view";
import { temporarySaveSelectedUsersToGroup } from "./SelectMembers.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    savedSelectedGroups: state.editUsersInGroup[ownProps.groupId]
});

const mapDispatchToProps = dispatch => ({
    temporarySaveSelectedUsersToGroup: (groupId, selectedUsers) =>
        dispatch(temporarySaveSelectedUsersToGroup(groupId, selectedUsers))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(SelectMembers);
