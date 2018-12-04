import { connect } from "react-redux";
import _ from "lodash";
import EditUserDetails from "./EditUserDetails.screen";

import { createGetWebsitesAction } from "../../../../api/websites/action-creator.websites.api";
import { createEditUserAction } from "../../../../api/users/action-creator.users.api";

const mapStateToProps = (state, ownProps) => ({
    user: _.find(state.users, { cid: ownProps.match.params.cid }),
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({
    usersChange: (user, cid) => dispatch(createEditUserAction(user, cid)),
    websitesLoad: () => dispatch(createGetWebsitesAction())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUserDetails);
