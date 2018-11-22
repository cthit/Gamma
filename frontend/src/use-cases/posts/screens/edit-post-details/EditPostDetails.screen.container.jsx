import { connect } from "react-redux";
import _ from "lodash";

import EditPostDetails from "./EditPostDetails.screen";

import { postsChange } from "../../Posts.action-creator";

const mapStateToProps = (state, ownProps) => ({
    post: _.find(state.posts, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    postsChange: (postData, postId) => dispatch(postsChange(postData, postId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditPostDetails);
