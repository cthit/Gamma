import React from "react";
import { Field } from "formik";

const GammaFormField = ({ name, component, componentProps }) => (
  <Field
    type="text"
    name={name}
    render={props => {
      const { field, form } = props;
      const error = form.touched[name] && form.errors[name];
      field.value = field.value == null ? "" : field.value;

      return React.createElement(component, {
        error: error != null,
        errorMessage: error,
        ...field,
        ...componentProps
      });
    }}
  />
);

export default GammaFormField;
