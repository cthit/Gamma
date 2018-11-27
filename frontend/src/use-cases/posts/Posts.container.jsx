import { connect } from "react-redux";
import { createGetPostsAction } from "../../api/posts/action-creator.posts.api";
import Posts from "./Posts";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    posts: state.posts
});

const mapDispatchToProps = dispatch => ({
    getPosts: () => dispatch(createGetPostsAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Posts);
