import { connect } from "react-redux";
import _ from "lodash";

import { groupsChange } from "../../Groups.action-creator";

import EditGroupDetails from "./EditGroupDetails.screen";

const mapStateToProps = (state, ownProps) => ({
    group: _.find(state.groups, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    groupsChange: (group, groupId) => dispatch(groupsChange(group, groupId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditGroupDetails);
