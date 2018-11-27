import { connect } from "react-redux";
import _ from "lodash";
import EditUserDetails from "./EditUserDetails.screen";

import { websitesLoad } from "../../../websites/Websites.action-creator";
import { createEditUserAction } from "../../../../api/users/action-creator.users.api";

const mapStateToProps = (state, ownProps) => ({
    user: _.find(state.users, { cid: ownProps.match.params.cid }),
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({
    usersChange: (user, cid) => dispatch(createEditUserAction(user, cid)),
    websitesLoad: () => dispatch(websitesLoad())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUserDetails);
