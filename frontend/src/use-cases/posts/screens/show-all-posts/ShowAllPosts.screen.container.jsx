import { connect } from "react-redux";
import ShowAllPosts from "./ShowAllPosts.screen";

const mapStateToProps = (state, ownProps) => ({
    posts: state.posts
});

const mapDispatchToProps = dispatch => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllPosts);
