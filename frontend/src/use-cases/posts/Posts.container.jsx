import { connect } from "react-redux";
import { postsLoad } from "./Posts.action-creator";
import Posts from "./Posts";

import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    posts: state.posts
});

const mapDispatchToProps = dispatch => ({
    postsLoad: () => dispatch(postsLoad()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Posts);
