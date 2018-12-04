import { connect } from "react-redux";
import _ from "lodash";

import { createEditGroupAction } from "../../../../api/groups/action-creator.groups.api";

import EditGroupDetails from "./EditGroupDetails.screen";

const mapStateToProps = (state, ownProps) => ({
    group: _.find(state.groups, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    groupsChange: (group, groupId) =>
        dispatch(createEditGroupAction(group, groupId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditGroupDetails);
