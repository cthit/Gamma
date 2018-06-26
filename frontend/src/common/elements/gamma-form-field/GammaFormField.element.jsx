import React from "react";
import { Field } from "formik";

const GammaFormField = ({ name, component, componentProps }) => (
  <Field
    name={name}
    render={({ field, form }) => {
      const nameValueInForm = form.values[name];
      const value = nameValueInForm != null ? nameValueInForm : "";

      const error = form.touched[name] && form.errors[name];
      const handleChange = form.handleChange;

      return React.createElement(component, {
        onChange: handleChange,
        value: value,
        error: error,
        ...componentProps,
        inputProps: {
          ...field
        }
      });
    }}
  />
);

export default GammaFormField;
