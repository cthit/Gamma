import { DigitToastActions } from "@cthit/react-digit-components";
import { connect } from "react-redux";
import { postsAdd } from "../../Posts.action-creator";
import AddNewPost from "./AddNewPost.screen";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  postsAdd: post => dispatch(postsAdd(post)),
  toastOpen: toastData => dispatch(DigitToastActions.digitToastOpen(toastData))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddNewPost);
