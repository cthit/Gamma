import React from "react";
import { FieldArray } from "formik";

const GammaFormFieldArray = ({ name, component, componentProps }) => (
  <FieldArray
    name={name}
    render={props => {
      const { field, form } = props;

      return React.createElement(component, {
        ...componentProps,
        ...field,
        ...props
      });
    }}
  />
);

export default GammaFormFieldArray;
