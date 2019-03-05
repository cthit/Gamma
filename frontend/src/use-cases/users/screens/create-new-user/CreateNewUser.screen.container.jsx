import { connect } from "react-redux";

import CreateNewUser from "./CreateNewUser.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createGetWebsitesAction } from "../../../../api/websites/action-creator.websites.api";
import { createAddUserAction } from "../../../../api/users/action-creator.users.api";

const mapStateToProps = (state, ownProps) => ({
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    websitesLoad: () => dispatch(createGetWebsitesAction()),
    addUser: userData => dispatch(createAddUserAction(userData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(CreateNewUser);
