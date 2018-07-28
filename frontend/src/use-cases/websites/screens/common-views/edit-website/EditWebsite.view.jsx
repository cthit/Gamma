import React from "react";
import * as yup from "yup";
import GammaEditData from "../../../../../common/elements/gamma-edit-data";
import GammaTextField from "../../../../../common/elements/gamma-text-field";

const EditWebsite = ({ initialValues, onSubmit, titleText, submitText }) => (
  <GammaEditData
    initialValues={initialValues}
    onSubmit={onSubmit}
    validationSchema={yup.object().shape({
      name: yup.string().required(),
      prettyName: yup.string().required()
    })}
    titleText={titleText}
    submitText={submitText}
    keysOrder={["name", "prettyName"]}
    keysComponentData={{
      name: {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Name"
        }
      },
      prettyName: {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Pretty name"
        }
      }
    }}
  />
);

export default EditWebsite;
