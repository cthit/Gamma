import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import loadTranslations from "../../../../common/utils/loaders/translations.loader";
import { websitesDelete } from "../../Websites.action-creator";
import ShowWebsiteDetails from "./ShowWebsiteDetails.screen";
import translations from "./ShowWebsiteDetails.screen.translations.json";

const mapStateToProps = (state, ownProps) => ({
    text: loadTranslations(
        state.localize,
        translations.ShowWebsiteDetails,
        "Websites.Screen.ShowWebsiteDetails."
    ),
    website: _.find(state.websites, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    websitesDelete: websiteId => dispatch(websitesDelete(websiteId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowWebsiteDetails);
