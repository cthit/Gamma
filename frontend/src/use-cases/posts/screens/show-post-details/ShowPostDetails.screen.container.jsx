import {
    DigitDialogActions,
    DigitRedirectActions,
    DigitToastActions
} from "@cthit/react-digit-components";
import _ from "lodash";
import { connect } from "react-redux";
import { postsDelete, postsLoadUsage } from "../../Posts.action-creator";
import ShowPostDetails from "./ShowPostDetails.screen";

const mapStateToProps = (state, ownProps) => ({
    post: _.find(state.posts, { id: ownProps.match.params.id }),
    postId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
    dialogOpen: options =>
        dispatch(DigitDialogActions.digitDialogOpen(options)),
    toastOpen: options => dispatch(DigitToastActions.digitToastOpen(options)),
    redirectTo: to => dispatch(DigitRedirectActions.redirectTo(to)),
    postsDelete: postId => dispatch(postsDelete(postId)),
    postsLoadUsage: postId => dispatch(postsLoadUsage(postId))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowPostDetails);
