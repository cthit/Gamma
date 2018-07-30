import React from "react";
import _ from "lodash";
import { withLocalize } from "react-localize-redux";
class GammaTranslations extends React.Component {
  constructor(props) {
    super();

    if (props.uniquePath != null && props.translations != null) {
      const translations = {};
      _.set(translations, props.uniquePath, props.translations);
      props.addTranslation(translations);
    }
  }

  render() {
    return this.props.render(this.props.text, this.props.activeLanguage);
  }
}

export default withLocalize(GammaTranslations);
