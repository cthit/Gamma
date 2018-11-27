import { connect } from "react-redux";

import AddNewWebsite from "./AddNewWebsite.screen";

import { createAddWebsiteAction } from "../../../../api/websites/action-creator.websites.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    websitesAdd: websiteData => dispatch(createAddWebsiteAction(websiteData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewWebsite);
