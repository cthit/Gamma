import { connect } from "react-redux";
import _ from "lodash";
import EditUsersInGroup from "./EditUsersInGroup.screen";

import { temporarySaveSelectedUsersToGroup } from "./EditUsersInGroup.screen.action-creator";
import { createGetUsersAction } from "../../../../api/users/action-creator.users.api";

const mapStateToProps = (state, ownProps) => ({
    users: state.users,
    group: _.find(state.groups, { id: ownProps.match.params.id }),
    savedSelectedGroups: state["editUsersInGroup"][ownProps.match.params.id]
});

const mapDispatchToProps = dispatch => ({
    loadUsers: () => dispatch(createGetUsersAction()),
    temporarySaveSelectedUsersToGroup: (groupId, selectedUsers) =>
        dispatch(temporarySaveSelectedUsersToGroup(groupId, selectedUsers))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUsersInGroup);
