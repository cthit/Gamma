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

const EditPost = ({
  initialValues,
  onSubmit,
  titleText,
  swedishInputText,
  englishInputText,
  submitText,
  fieldRequiredText
}) => (
  <GammaForm
    validationSchema={yup.object().shape({
      sv: yup.string().required(fieldRequiredText),
      en: yup.string().required(fieldRequiredText)
    })}
    initialValues={initialValues}
    onSubmit={(values, actions) => {
      const wrapped = {
        post: {
          ...values
        }
      };

      onSubmit(wrapped, actions);
    }}
    render={({ errors, touched }) => (
      <GammaCard minWidth="300px" maxWidth="600px">
        <GammaCardTitle text={titleText} />
        <GammaCardBody>
          <GammaFormField
            name="sv"
            component={GammaTextField}
            componentProps={{ upperLabel: swedishInputText }}
          />

          <GammaFormField
            name="en"
            component={GammaTextField}
            componentProps={{ upperLabel: englishInputText }}
          />
        </GammaCardBody>
        <GammaCardButtons reverseDirection>
          <GammaButton submit text={submitText} raised primary />
        </GammaCardButtons>
      </GammaCard>
    )}
  />
);

export default EditPost;
