import { connect } from "react-redux";
import ContainUserToAllowedPages from "./ContainUserToAllowedPages.declarative";
import { redirectTo } from "../../../app/views/gamma-redirect/GammaRedirect.view.action-creator";

const mapStateToProps = (state, ownProps) => ({});

const mapDispatchToProps = dispatch => ({
  redirectTo: to => dispatch(redirectTo(to))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ContainUserToAllowedPages);
