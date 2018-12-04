import { DigitToastActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { createAddPostAction } from "../../../../api/posts/action-creator.posts.api";
import AddNewPost from "./AddNewPost.screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    addPost: post => dispatch(createAddPostAction(post)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewPost);
