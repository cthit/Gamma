import AddNewPost from "./AddNewPost.screen";
import { connect } from "react-redux";

import { postsAdd } from "../../Posts.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  postsAdd: post => dispatch(postsAdd(post)),
  toastOpen: toastData => dispatch(toastOpen(toastData))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddNewPost);
