import { connect } from "react-redux";
import _ from "lodash";

import translations from "./ShowPostDetails.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import ShowPostDetails from "./ShowPostDetails.screen";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.ShowPostDetails,
    "Posts.Screen.ShowPostDetails."
  ),
  post: _.find(state.posts, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowPostDetails);
