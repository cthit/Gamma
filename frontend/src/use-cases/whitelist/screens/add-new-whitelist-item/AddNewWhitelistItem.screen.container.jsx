import { connect } from "react-redux";
import AddNewWhitelistItem from "./AddNewWhitelistItem.screen";

import { whitelistAdd } from "../../Whitelist.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    whitelistAdd: newWhitelistAdd => dispatch(whitelistAdd(newWhitelistAdd))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewWhitelistItem);
