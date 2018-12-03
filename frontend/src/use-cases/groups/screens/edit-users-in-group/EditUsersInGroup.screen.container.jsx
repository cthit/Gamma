import { connect } from "react-redux";
import _ from "lodash";
import EditUsersInGroup from "./EditUsersInGroup.screen";

import { createGetUsersAction } from "../../../../api/users/action-creator.users.api";

const mapStateToProps = (state, ownProps) => ({
    users: state.users,
    group: _.find(state.groups, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    loadUsers: () => dispatch(createGetUsersAction())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUsersInGroup);
