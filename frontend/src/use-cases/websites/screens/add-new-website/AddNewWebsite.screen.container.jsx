import { connect } from "react-redux";

import AddNewWebsite from "./AddNewWebsite.screen";

import { DigitToastActions } from "@cthit/react-digit-components";

import { createAddWebsiteAction } from "../../../../api/websites/action-creator.websites.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    websitesAdd: websiteData => dispatch(createAddWebsiteAction(websiteData)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewWebsite);
