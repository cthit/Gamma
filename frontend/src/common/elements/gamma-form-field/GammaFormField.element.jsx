import React from "react";
import { Field } from "formik";

const GammaFormField = ({ name, component, componentProps }) => (
  <Field
    name={name}
    render={({ field, form }) => {
      const error = form.touched[name] && form.errors[name];

      return React.createElement(component, {
        error: error,
        ...field,
        ...componentProps
      });
    }}
  />
);

export default GammaFormField;
