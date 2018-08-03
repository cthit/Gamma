import { connect } from "react-redux";
import _ from "lodash";

import ShowPostDetails from "./ShowPostDetails.screen";
import { gammaDialogOpen } from "../../../../app/views/gamma-dialog/GammaDialog.view.action-creator";
import { toastOpen } from "../../../../app/views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { postsDelete, postsLoadUsage } from "../../Posts.action-creator";

const mapStateToProps = (state, ownProps) => ({
  post: _.find(state.posts, { id: ownProps.match.params.id }),
  postId: ownProps.match.params.id
});

const mapDispatchToProps = dispatch => ({
  gammaDialogOpen: options => dispatch(gammaDialogOpen(options)),
  toastOpen: options => dispatch(toastOpen(options)),
  redirectTo: to => dispatch(redirectTo(to)),
  postsDelete: postId => dispatch(postsDelete(postId)),
  postsLoadUsage: postId => dispatch(postsLoadUsage(postId))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ShowPostDetails);
