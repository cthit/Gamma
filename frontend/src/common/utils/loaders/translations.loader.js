import { getTranslate } from "react-localize-redux";
import common from "../translations/CommonTranslations.json";
import _ from "lodash";

/*
* Also loads in common by default
* baseUrl must contain a dot at the end.
*/
export default function loadTranslations(localize, translations, baseUrl) {
  const translate = textId => getTranslate(localize)(baseUrl + textId);

  const textsToTranslate = _.keys(translations);

  const texts = _.merge(
    {},
    _.zipObject(
      textsToTranslate,
      _.map(textsToTranslate, text => translate(text))
    ),
    _loadCommonTranslations(localize)
  );
  return texts;
}

function _loadCommonTranslations(localize) {
  const baseUrl = "Common.";

  const translate = textId => getTranslate(localize)(baseUrl + textId);

  const textsToTranslate = _.keys(common.Common);

  return _.zipObject(
    textsToTranslate,
    _.map(textsToTranslate, text => translate(text))
  );
}
