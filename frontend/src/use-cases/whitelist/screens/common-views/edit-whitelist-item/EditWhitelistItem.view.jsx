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

const EditWhitelistItem = ({
  onSubmit,
  initialValues,
  titleText,
  cidInputText,
  fieldRequiredText,
  submitText
}) => (
  <GammaForm
    validationSchema={yup.object().shape({
      cid: yup.string().required(fieldRequiredText)
    })}
    initialValues={initialValues}
    onSubmit={(values, actions) => {
      onSubmit(values, actions);
    }}
    render={({ errors, touched }) => (
      <GammaCard minWidth="300px" maxWidth="600px">
        <GammaCardTitle text={titleText} />
        <GammaCardBody>
          <GammaFormField
            name="cid"
            component={GammaTextField}
            componentProps={{ upperLabel: cidInputText }}
          />
        </GammaCardBody>
        <GammaCardButtons reverseDirection>
          <GammaButton submit text={submitText} raised primary />
        </GammaCardButtons>
      </GammaCard>
    )}
  />
);

export default EditWhitelistItem;
