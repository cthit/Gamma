import React from "react";
import * as yup from "yup";

import GammaEditData from "../../../../../common/elements/gamma-edit-data";
import GammaTextField from "../../../../../common/elements/gamma-text-field";

const EditWhitelistItem = ({
  onSubmit,
  initialValues,
  titleText,
  cidInputText,
  fieldRequiredText,
  submitText
}) => (
  <GammaEditData
    validationSchema={yup.object().shape({
      cid: yup.string().required(fieldRequiredText)
    })}
    initialValues={initialValues}
    onSubmit={onSubmit}
    titleText={titleText}
    submitText={submitText}
    keysOrder={["cid"]}
    keysComponentData={{
      cid: {
        component: GammaTextField,
        componentProps: {
          upperLabel: cidInputText
        }
      }
    }}
  />
);

export default EditWhitelistItem;
