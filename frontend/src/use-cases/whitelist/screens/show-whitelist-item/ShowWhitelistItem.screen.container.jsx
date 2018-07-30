import { connect } from "react-redux";
import _ from "lodash";
import ShowWhitelistItem from "./ShowWhitelistItem.screen";

const mapStateToProps = (state, ownProps) => ({
  whitelistItem: _.find(state.whitelist, { id: ownProps.match.params.id })
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowWhitelistItem);
