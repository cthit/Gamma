import { connect } from "react-redux";
import ShowAllPosts from "./ShowAllPosts.screen";
import { createGetPostsAction } from "../../../../api/posts/action-creator.posts.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    posts: state.posts
});

const mapDispatchToProps = dispatch => ({
    getPosts: () => dispatch(createGetPostsAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllPosts);
