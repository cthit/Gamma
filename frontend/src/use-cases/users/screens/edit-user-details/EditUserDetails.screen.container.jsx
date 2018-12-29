import { connect } from "react-redux";
import _ from "lodash";
import EditUserDetails from "./EditUserDetails.screen";

import { createGetWebsitesAction } from "../../../../api/websites/action-creator.websites.api";
import {
    createEditUserAction,
    createGetUserAction
} from "../../../../api/users/action-creator.users.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    user: _.find(state.users, { cid: ownProps.match.params.cid }),
    userCid: ownProps.match.params.cid,
    websites: state.websites
});

const mapDispatchToProps = dispatch => ({
    usersChange: (user, cid) => dispatch(createEditUserAction(user, cid)),
    websitesLoad: () => dispatch(createGetWebsitesAction()),
    getUser: cid => dispatch(createGetUserAction(cid)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUserDetails);
