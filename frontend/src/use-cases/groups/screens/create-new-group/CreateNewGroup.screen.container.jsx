import { connect } from "react-redux";
import CreateNewGroup from "./CreateNewGroup.screen";

import { groupsAdd } from "../../Groups.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    groupsAdd: group => dispatch(groupsAdd(group))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateNewGroup);
