import React from "react";
import PropTypes from "prop-types";
import { Formik, Form } from "formik";

const GammaForm = ({ initialValues, onSubmit, validationSchema, render }) => (
  <Formik
    validationSchema={validationSchema}
    initialValues={{ ...initialValues }}
    onSubmit={onSubmit}
    render={formData => <Form>{render(formData)}</Form>}
  />
);

GammaForm.propTypes = {
  initialValues: PropTypes.object,
  onSubmit: PropTypes.func.isRequired,
  validationSchema: PropTypes.object,
  render: PropTypes.func.isRequired
};

export default GammaForm;
