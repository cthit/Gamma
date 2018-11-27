import { connect } from "react-redux";
import _ from "lodash";

import EditPostDetails from "./EditPostDetails.screen";

import { createEditPostAction } from "../../../../api/posts/action-creator.posts.api";

const mapStateToProps = (state, ownProps) => ({
    post: _.find(state.posts, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    editPost: (postData, postId) =>
        dispatch(createEditPostAction(postData, postId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditPostDetails);
