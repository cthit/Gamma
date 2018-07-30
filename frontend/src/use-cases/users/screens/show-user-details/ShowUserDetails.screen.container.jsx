import { connect } from "react-redux";
import _ from "lodash";
import ShowUserDetails from "./ShowUserDetails.screen";

const mapStateToProps = (state, ownProps) => ({
  user: _.find(state.users, { cid: ownProps.match.params.cid })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowUserDetails);
