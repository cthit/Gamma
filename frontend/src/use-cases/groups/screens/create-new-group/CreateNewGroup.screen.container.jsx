import { connect } from "react-redux";
import CreateNewGroup from "./CreateNewGroup.screen";

import { createAddGroupAction } from "../../../../api/groups/action-creator.groups.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    groupsAdd: group => dispatch(createAddGroupAction(group))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateNewGroup);
