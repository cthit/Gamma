import React from "react";
import * as yup from "yup";
import GammaForm from "../../../../../common/elements/gamma-form";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardButtons,
  GammaCardBody
} from "../../../../../common-ui/design";
import GammaFormField from "../../../../../common/elements/gamma-form-field";
import GammaButton from "../../../../../common/elements/gamma-button";
import GammaTextField from "../../../../../common/elements/gamma-text-field";
import GammaEditData from "../../../../../common/elements/gamma-edit-data";

const EditPost = ({
  initialValues,
  onSubmit,
  titleText,
  swedishInputText,
  englishInputText,
  submitText,
  fieldRequiredText
}) => (
  <GammaEditData
    initialValues={initialValues}
    onSubmit={(values, actions) => {
      const wrapped = {
        post: {
          ...values
        }
      };
      onSubmit(wrapped, actions);
    }}
    validationSchema={yup.object().shape({
      sv: yup.string().required(fieldRequiredText),
      en: yup.string().required(fieldRequiredText)
    })}
    titleText={titleText}
    submitText={submitText}
    keysOrder={["sv", "en"]}
    keysComponentData={{
      sv: {
        component: GammaTextField,
        componentProps: {
          upperLabel: swedishInputText
        }
      },
      en: {
        component: GammaTextField,
        componnetProps: {
          upperLabel: englishInputText
        }
      }
    }}
  />
);

export default EditPost;
