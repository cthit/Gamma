import { connect } from "react-redux";
import _ from "lodash";
import ShowUserDetails from "./ShowUserDetails.screen";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";
import translations from "./ShowUserDetails.screen.translations";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.ShowUserDetails,
    "Users.Screen.ShowUserDetails."
  ),
  user: _.find(state.users, { cid: ownProps.match.params.cid })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowUserDetails);
