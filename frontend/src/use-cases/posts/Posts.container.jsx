import { connect } from "react-redux";
import { postsLoad } from "./Posts.action-creator";
import Posts from "./Posts";
import loadTranslations from "../../common/utils/loaders/translations.loader";
import translations from "./Posts.translations.json";

const mapStateToProps = (state, ownProps) => ({
  posts: state.posts,
  text: loadTranslations(state.localize, translations.Posts, "Posts.")
});

const mapDispatchToProps = dispatch => ({
  postsLoad: () => dispatch(postsLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Posts);
