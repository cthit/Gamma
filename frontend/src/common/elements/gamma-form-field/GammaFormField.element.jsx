import React from "react";
import { Field } from "formik";

const GammaFormField = ({ name, component, componentProps }) => (
  <Field
    name={name}
    render={({ field, form }) => {
      console.log(field);
      console.log(form);
      const nameValueInForm = form.values[name];
      const value = nameValueInForm != null ? nameValueInForm : "";

      const error = form.touched[name] && form.errors[name];
      const handleChange = form.handleChange;
      const handleBlur = form.handleBlur;
      return React.createElement(component, {
        onChange: handleChange,
        onBlur: handleBlur,
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
