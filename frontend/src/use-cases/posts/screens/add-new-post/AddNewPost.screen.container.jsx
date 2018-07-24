import AddNewPost from "./AddNewPost.screen";
import { connect } from "react-redux";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";
import translations from "./AddNewPost.screen.translations.json";

import { postsAdd } from "../../Posts.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.AddNewPost,
    "Posts.Screen.AddNewPost."
  )
});

const mapDispatchToProps = dispatch => ({
  postsAdd: post => dispatch(postsAdd(post))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddNewPost);
