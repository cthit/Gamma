import { connect } from "react-redux";

import translations from "./EditPostDetails.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import EditPostDetails from "./EditPostDetails.screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditPostDetails);
