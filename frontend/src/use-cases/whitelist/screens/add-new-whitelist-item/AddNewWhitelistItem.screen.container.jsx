import { connect } from "react-redux";
import AddNewWhitelistItem from "./AddNewWhitelistItem.screen";

import { createAddToWhitelistAction } from "../../../../api/whitelist/action-creator.whitelist.api";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    whitelistAdd: newWhitelistAdd =>
        dispatch(createAddToWhitelistAction(newWhitelistAdd))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewWhitelistItem);
