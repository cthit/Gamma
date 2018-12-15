import { connect } from "react-redux";
import _ from "lodash";
import EditUsersInGroup from "./EditUsersInGroup.screen";

import { temporarySaveSelectedUsersToGroup } from "./EditUsersInGroup.screen.action-creator";
import { createGetUsersAction } from "../../../../api/users/action-creator.users.api";
import { createGetGroupAction } from "../../../../api/groups/action-creator.groups.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    users: state.users,
    group: _.find(state.groups, { id: ownProps.match.params.id }),
    groupId: ownProps.match.params.id,
    savedSelectedGroups: state["editUsersInGroup"][ownProps.match.params.id]
});

const mapDispatchToProps = dispatch => ({
    getGroup: groupId => dispatch(createGetGroupAction(groupId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
    loadUsers: () => dispatch(createGetUsersAction()),
    temporarySaveSelectedUsersToGroup: (groupId, selectedUsers) =>
        dispatch(temporarySaveSelectedUsersToGroup(groupId, selectedUsers))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUsersInGroup);
