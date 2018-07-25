import { connect } from "react-redux";
import _ from "lodash";
import translations from "./EditPostDetails.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import EditPostDetails from "./EditPostDetails.screen";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.EditPostDetails,
    "Posts.Screen.EditPostDetails."
  ),
  post: _.find(state.posts, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditPostDetails);
