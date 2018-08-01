import { connect } from "react-redux";
import _ from "lodash";
import EditUserDetails from "./EditUserDetails.screen";

import { usersChange } from "../../Users.action-creator";

const mapStateToProps = (state, ownProps) => ({
  user: _.find(state.users, { cid: ownProps.match.params.cid })
});

const mapDispatchToProps = dispatch => ({
  usersChange: (user, cid) => dispatch(usersChange(user, cid))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditUserDetails);
