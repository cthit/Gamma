import { connect } from "react-redux";

import ShowWhitelist from "./ShowWhitelist.screen";

const mapStateToProps = (state, ownProps) => ({
    whitelist: state.whitelist
});

const mapDispatchToProps = dispatch => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowWhitelist);
