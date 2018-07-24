import { connect } from "react-redux";

import ShowAllPosts from "./ShowAllPosts.screen";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";
import translations from "./ShowAllPosts.screen.translations.json";

const mapStateToProps = (state, ownProps) => ({
  posts: state.posts
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowAllPosts);
