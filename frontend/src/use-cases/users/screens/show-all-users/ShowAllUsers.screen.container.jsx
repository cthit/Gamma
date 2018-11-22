import { connect } from "react-redux";
import ShowAllUsers from "./ShowAllUsers.screen";

const mapStateToProps = (state, ownProps) => ({
    users: state.users
});

const mapDispatchToProps = dispatch => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShowAllUsers);
