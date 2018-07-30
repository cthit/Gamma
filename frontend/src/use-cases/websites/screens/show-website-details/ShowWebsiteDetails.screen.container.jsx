import { connect } from "react-redux";
import _ from "lodash";

import ShowWebsiteDetails from "./ShowWebsiteDetails.screen";
import translations from "./ShowWebsiteDetails.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.ShowWebsiteDetails,
    "Websites.Screen.ShowWebsiteDetails."
  ),
  website: _.find(state.websites, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowWebsiteDetails);
