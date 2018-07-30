import { connect } from "react-redux";
import _ from "lodash";

import ShowPostDetails from "./ShowPostDetails.screen";

const mapStateToProps = (state, ownProps) => ({
  post: _.find(state.posts, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowPostDetails);
