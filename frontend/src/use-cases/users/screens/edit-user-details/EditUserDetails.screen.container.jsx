import { connect } from "react-redux";
import _ from "lodash";
import EditUserDetails from "./EditUserDetails.screen";

import loadTranslations from "../../../../common/utils/loaders/translations.loader";
import translations from "./EditUserDetails.screen.translations";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.EditUserDetails,
    "Users.Screen.EditUserDetails."
  ),
  user: _.find(state.users, { cid: ownProps.match.params.cid })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditUserDetails);
