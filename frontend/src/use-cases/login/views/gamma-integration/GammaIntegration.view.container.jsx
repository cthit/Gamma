import { connect } from "react-redux";

import { DigitRedirectActions } from "@cthit/react-digit-components";

import GammaIntegration from "./GammaIntegration.view";

const mapStateToProps = () => ({});

const mapDispatchToProps = dispatch => ({
    redirectTo: to => dispatch(DigitRedirectActions.digitRedirectTo(to))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(GammaIntegration);
