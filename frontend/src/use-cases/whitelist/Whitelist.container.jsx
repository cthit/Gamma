import { connect } from "react-redux";
import Whitelist from "./Whitelist";

import { whitelistLoad } from "./Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  whitelistLoad: () => dispatch(whitelistLoad())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Whitelist);
