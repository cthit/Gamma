import { connect } from "react-redux";
import _ from "lodash";

import GammaTranslations from "./GammaTranslations.declaratives";
import loadTranslations from "../../utils/loaders/translations.loader";

const mapStateToProps = (state, ownProps) => {
    const translations = {};
    _.set(translations, ownProps.uniquePath, ownProps.translations);
    return {
        text: ownProps.onlyCommon
            ? loadTranslations(state.localize)
            : loadTranslations(
                  state.localize,
                  translations,
                  ownProps.uniquePath
              )
    };
};

const mapDispatchToProps = dispatch => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(GammaTranslations);
