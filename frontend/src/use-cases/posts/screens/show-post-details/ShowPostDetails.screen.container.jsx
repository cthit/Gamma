import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import {
    createDeletePostAction,
    createGetPostAction,
    createGetPostUsageAction
} from "../../../../api/posts/action-creator.posts.api";
import ShowPostDetails from "./ShowPostDetails.screen";
import {
    gammaLoadingFinished,
    gammaLoadingStart
} from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({
    post: _.find(state.posts, { id: ownProps.match.params.id }),
    postId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    toastOpen: options => dispatch(DigitToastActions.digitToastOpen(options)),
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to)),
    deletePost: postId => dispatch(createDeletePostAction(postId)),
    getPostUsages: postId => dispatch(createGetPostUsageAction(postId)),
    getPost: postId => dispatch(createGetPostAction(postId)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished()),
    gammaLoadingStart: () => dispatch(gammaLoadingStart())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowPostDetails);
