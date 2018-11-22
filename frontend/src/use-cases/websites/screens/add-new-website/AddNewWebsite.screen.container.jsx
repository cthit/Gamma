import { connect } from "react-redux";

import AddNewWebsite from "./AddNewWebsite.screen";

import { websitesAdd } from "../../Websites.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    websitesAdd: websiteData => dispatch(websitesAdd(websiteData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewWebsite);
