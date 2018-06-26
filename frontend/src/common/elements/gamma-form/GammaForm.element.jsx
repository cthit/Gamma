import React from "react";
import { Formik, Form } from "formik";

const GammaForm = ({ initialValues, onSubmit, validationSchema, render }) => (
  <Formik
    validationSchema={validationSchema}
    initialValues={{ ...initialValues }}
    onSubmit={onSubmit}
    render={formData => <Form>{render(formData)}</Form>}
  />
);

export default GammaForm;
