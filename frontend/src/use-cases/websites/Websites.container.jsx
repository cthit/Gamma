import { connect } from "react-redux";

import Websites from "./Websites";

import { websitesLoad } from "./Websites.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  websitesLoad: () => dispatch(websitesLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Websites);
