import { connect } from "react-redux";
import { postsLoad } from "./Posts.action-creator";
import Posts from "./Posts";

const mapStateToProps = (state, ownProps) => ({
  posts: state.posts
});

const mapDispatchToProps = dispatch => ({
  postsLoad: () => dispatch(postsLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Posts);
