import { connect } from "react-redux";
import _ from "lodash";

import ShowWebsiteDetails from "./ShowWebsiteDetails.screen";
import translations from "./ShowWebsiteDetails.screen.translations.json";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";

import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { gammaDialogOpen } from "../../../../app/views/gamma-dialog/GammaDialog.view.action-creator";
import { websitesDelete } from "../../Websites.action-creator";

const mapStateToProps = (state, ownProps) => ({
  text: loadTranslations(
    state.localize,
    translations.ShowWebsiteDetails,
    "Websites.Screen.ShowWebsiteDetails."
  ),
  website: _.find(state.websites, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
  toastOpen: toastData => dispatch(toastOpen(toastData)),
  redirectTo: to => dispatch(redirectTo(to)),
  gammaDialogOpen: options => dispatch(gammaDialogOpen(options)),
  websitesDelete: websiteId => dispatch(websitesDelete(websiteId))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowWebsiteDetails);
