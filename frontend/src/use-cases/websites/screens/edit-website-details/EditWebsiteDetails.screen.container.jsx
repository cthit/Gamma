import { connect } from "react-redux";
import _ from "lodash";
import EditWebsiteDetails from "./EditWebsiteDetails.screen";

import { DigitToastActions } from "@cthit/react-digit-components";

import { createEditWebsiteAction } from "../../../../api/websites/action-creator.websites.api";

const mapStateToProps = (state, ownProps) => ({
    website: _.find(state.websites, { id: ownProps.match.params.id }),
    websiteId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    websitesChange: (websiteData, websiteId) =>
        dispatch(createEditWebsiteAction(websiteData, websiteId)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditWebsiteDetails);
