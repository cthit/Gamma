import { connect } from "react-redux";
import EditUsersInGroup from "./EditUsersInGroup.screen";

import { createGetUsersMinifiedAction } from "../../../../api/users/action-creator.users.api";
import { createGetGroupAction } from "../../../../api/groups/action-creator.groups.api";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { createGetPostsAction } from "../../../../api/posts/action-creator.posts.api";

import { DigitRedirectActions } from "@cthit/react-digit-components";

const mapStateToProps = (state, ownProps) => ({
    users: state.users,
    group: state.groups != null ? state.groups.details : null,
    groupId: ownProps.match.params.id,
    posts: state.posts,
    route: ownProps.location.pathname
});

const mapDispatchToProps = dispatch => ({
    getGroup: groupId => dispatch(createGetGroupAction(groupId)),
    getPosts: () => dispatch(createGetPostsAction()),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart()),
    loadUsers: () => dispatch(createGetUsersMinifiedAction()),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(EditUsersInGroup);
