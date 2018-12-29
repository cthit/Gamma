import { connect } from "react-redux";
import _ from "lodash";

import EditPostDetails from "./EditPostDetails.screen";

import {
    createEditPostAction,
    createGetPostAction
} from "../../../../api/posts/action-creator.posts.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    post: _.find(state.posts, { id: ownProps.match.params.id }),
    postId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    editPost: (postData, postId) =>
        dispatch(createEditPostAction(postData, postId)),
    getPost: postId => dispatch(createGetPostAction(postId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditPostDetails);
