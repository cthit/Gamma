import { DigitToastActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { createAddPostAction } from "../../../../api/posts/action-creator.posts.api";
import AddNewPost from "./AddNewPost.screen";
import { gammaLoadingFinished } from "../../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
    addPost: post => dispatch(createAddPostAction(post)),
    toastOpen: toastData =>
        dispatch(DigitToastActions.digitToastOpen(toastData)),
    gammaLoadingFinished: () => dispatch(gammaLoadingFinished())
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AddNewPost);
