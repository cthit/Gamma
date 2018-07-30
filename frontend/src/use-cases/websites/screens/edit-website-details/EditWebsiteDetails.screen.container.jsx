import { connect } from "react-redux";
import _ from "lodash";
import EditWebsiteDetails from "./EditWebsiteDetails.screen";

import { websitesChange } from "../../Websites.action-creator";

const mapStateToProps = (state, ownProps) => ({
  website: _.find(state.websites, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({
  websitesChange: (websiteData, websiteId) =>
    dispatch(websitesChange(websiteData, websiteId))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditWebsiteDetails);
