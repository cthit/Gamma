import { connect } from "react-redux";

import ShowAllWebsites from "./ShowAllWebsites.screen";

const mapStateToProps = (state, ownProps) => ({
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllWebsites);
